package com.navershop.navershop.template.service;

import com.navershop.navershop.core.api.NaverShoppingApiClient;
import com.navershop.navershop.core.dto.NaverShoppingResponse;
import com.navershop.navershop.template.adapter.mapper.ProductMapper;
import com.navershop.navershop.template.adapter.option.OptionGenerator;
import com.navershop.navershop.template.adapter.provider.category.CategoryProvider;
import com.navershop.navershop.template.adapter.provider.product.ProductProvider;
import com.navershop.navershop.template.adapter.provider.user.UserProvider;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * WebClient를 사용하는 크롤링 서비스
 */
@Slf4j
public abstract class BaseCrawlingService<PRODUCT, CATEGORY, USER> {

    protected final NaverShoppingApiClient apiClient;
    protected final ProductMapper<PRODUCT, CATEGORY, USER> productMapper;
    protected final ProductProvider<PRODUCT> productProvider;
    protected final CategoryProvider<CATEGORY> categoryProvider;
    protected final UserProvider<USER> userProvider;
    protected final OptionGenerator<PRODUCT> optionGenerator;

    protected BaseCrawlingService(
            NaverShoppingApiClient apiClient,
            ProductMapper<PRODUCT, CATEGORY, USER> productMapper,
            ProductProvider<PRODUCT> productProvider,
            CategoryProvider<CATEGORY> categoryProvider,
            UserProvider<USER> userProvider,
            OptionGenerator<PRODUCT> optionGenerator) {
        this.apiClient = apiClient;
        this.productMapper = productMapper;
        this.productProvider = productProvider;
        this.categoryProvider = categoryProvider;
        this.userProvider = userProvider;
        this.optionGenerator = optionGenerator;
    }

    /**
     * 🚀 최고 성능: Reactive 방식
     *
     * 특징:
     * - WebClient의 Non-blocking I/O 활용
     * - 가장 빠른 성능
     * - 메모리 효율적
     */
    public CrawlingResult crawlAllCategoriesReactive(Long userId, int productsPerCategory) {
        log.info("===== 🚀 Reactive 크롤링 시작 =====");
        long startTime = System.currentTimeMillis();

        USER adminUser = userProvider.findById(userId);
        List<CATEGORY> targetCategories = findLeafCategories();
        log.info("검색 대상 카테고리 수: {}", targetCategories.size());

        AtomicInteger totalProducts = new AtomicInteger(0);
        AtomicInteger successCategories = new AtomicInteger(0);
        AtomicInteger failedCategories = new AtomicInteger(0);
        Map<Long, CategoryResult> categoryResults = new ConcurrentHashMap<>();

        // CompletableFuture로 병렬 처리 (Reactive와 호환)
        List<CompletableFuture<Void>> futures = targetCategories.stream()
                .map(category -> CompletableFuture.runAsync(() -> {
                    Long categoryId = categoryProvider.getCategoryId(category);
                    String categoryName = categoryProvider.getCategoryName(category);

                    try {
                        log.info("카테고리 '{}' 크롤링 시작... [Thread: {}]",
                                categoryName, Thread.currentThread().getName());

                        // Reactive 방식으로 크롤링
                        int savedCount = crawlAndSaveByCategoryReactive(
                                category, adminUser, productsPerCategory);

                        if (savedCount > 0) {
                            categoryResults.put(categoryId, CategoryResult.success(
                                    categoryId, categoryName, savedCount));
                            totalProducts.addAndGet(savedCount);
                            successCategories.incrementAndGet();
                            log.info("카테고리 '{}' 완료: {}개 저장", categoryName, savedCount);
                        } else {
                            categoryResults.put(categoryId, CategoryResult.noResults(
                                    categoryId, categoryName));
                            log.warn("카테고리 '{}'에서 검색 결과 없음", categoryName);
                        }

                    } catch (Exception e) {
                        log.error("카테고리 '{}' 크롤링 실패: {}", categoryName, e.getMessage(), e);
                        categoryResults.put(categoryId, CategoryResult.failed(
                                categoryId, categoryName, e.getMessage()));
                        failedCategories.incrementAndGet();
                    }
                }))
                .collect(Collectors.toList());

        // 모든 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        log.info("===== ✅ Reactive 크롤링 완료 =====");
        log.info("총 카테고리: {}, 성공: {}, 실패: {}, 총 상품: {}, 소요시간: {}초",
                targetCategories.size(), successCategories.get(), failedCategories.get(),
                totalProducts.get(), duration);

        return CrawlingResult.builder()
                .totalCategories(targetCategories.size())
                .successCategories(successCategories.get())
                .failedCategories(failedCategories.get())
                .totalProducts(totalProducts.get())
                .durationSeconds(duration)
                .categoryResults(new ArrayList<>(categoryResults.values()))
                .build();
    }

    /**
     * 카테고리별 크롤링 (Reactive 방식)
     */
    @Transactional
    protected int crawlAndSaveByCategoryReactive(CATEGORY category, USER seller, int count) {
        String categoryName = categoryProvider.getCategoryName(category);
        String keyword = buildFullCategoryPath(category);

        log.info("검색 키워드: '{}' (카테고리: '{}')", keyword, categoryName);

        int display = Math.min(count, 100);

        // 🚀 Reactive 방식으로 API 호출
        NaverShoppingResponse response = apiClient.searchMultiplePagesReactive(
                keyword, count, display, "sim");

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            return 0;
        }

        // 병렬 스트림으로 Product 변환
        List<PRODUCT> products = response.getItems().stream()
                .limit(count)
                .parallel()
                .map(item -> {
                    PRODUCT product = productMapper.map(item, category, seller);

                    // 옵션 생성
                    if (optionGenerator != null && optionGenerator.needsOptions(categoryName)) {
                        optionGenerator.generateAndAddOptions(product, categoryName);
                    }

                    return product;
                })
                .collect(Collectors.toList());

        log.info("{}개 상품 변환 완료", products.size());

        // 배치 저장
        return saveProductsBatch(products);
    }

    /**
     * 배치 저장
     */
    protected int saveProductsBatch(List<PRODUCT> products) {
        if (products.isEmpty()) {
            return 0;
        }

        log.info("💾 배치 저장 중... ({}개)", products.size());

        // 중복 체크를 병렬로 수행
        List<PRODUCT> nonDuplicates = products.stream()
                .parallel()
                .filter(product -> !productProvider.isDuplicate(product))
                .collect(Collectors.toList());

        log.info("중복 제거 후: {}개", nonDuplicates.size());

        // 배치 저장
        int savedCount = 0;
        int batchSize = 50;

        for (int i = 0; i < nonDuplicates.size(); i += batchSize) {
            int end = Math.min(i + batchSize, nonDuplicates.size());
            List<PRODUCT> batch = nonDuplicates.subList(i, end);

            try {
                for (PRODUCT product : batch) {
                    productProvider.save(product);
                    savedCount++;
                }
                log.debug("배치 저장 완료: {}-{}", i, end);
            } catch (Exception e) {
                log.error("배치 저장 실패: {}-{}", i, end, e);
            }
        }

        return savedCount;
    }

    /**
     * 리프 노드 카테고리 조회
     */
    protected List<CATEGORY> findLeafCategories() {
        List<CATEGORY> allCategories = categoryProvider.findAllCategories();
        Set<Long> parentIds = new HashSet<>();

        for (CATEGORY category : allCategories) {
            Long parentId = categoryProvider.getParentCategoryId(category);
            if (parentId != null) {
                parentIds.add(parentId);
            }
        }

        List<CATEGORY> leafCategories = allCategories.stream()
                .filter(category -> !parentIds.contains(
                        categoryProvider.getCategoryId(category)))
                .collect(Collectors.toList());

        log.info("전체 카테고리: {}개, 리프 카테고리: {}개",
                allCategories.size(), leafCategories.size());
        return leafCategories;
    }

    /**
     * 전체 카테고리 경로 생성
     */
    protected String buildFullCategoryPath(CATEGORY category) {
        List<String> pathNames = new ArrayList<>();
        CATEGORY current = category;

        while (current != null) {
            String name = categoryProvider.getCategoryName(current);
            pathNames.add(name);

            Long parentId = categoryProvider.getParentCategoryId(current);
            if (parentId != null) {
                current = categoryProvider.findById(parentId);
            } else {
                break;
            }
        }

        Collections.reverse(pathNames);
        String fullPath = String.join(" ", pathNames);
        return sanitizeKeyword(fullPath);
    }

    protected String sanitizeKeyword(String keyword) {
        if (keyword == null) return "";

        return keyword
                .replace("+", " ")
                .replace("·", " ")
                .replace("、", " ")
                .replace("，", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    @Data
    @Builder
    public static class CrawlingResult {
        private Integer totalCategories;
        private Integer successCategories;
        private Integer failedCategories;
        private Integer totalProducts;
        private Long durationSeconds;
        private List<CategoryResult> categoryResults;
    }

    @Data
    @Builder
    public static class CategoryResult {
        private Long categoryId;
        private String categoryName;
        private Integer productCount;
        private String status;
        private String error;

        public static CategoryResult success(Long id, String name, Integer count) {
            return CategoryResult.builder()
                    .categoryId(id)
                    .categoryName(name)
                    .productCount(count)
                    .status("SUCCESS")
                    .build();
        }

        public static CategoryResult noResults(Long id, String name) {
            return CategoryResult.builder()
                    .categoryId(id)
                    .categoryName(name)
                    .productCount(0)
                    .status("NO_RESULTS")
                    .build();
        }

        public static CategoryResult failed(Long id, String name, String error) {
            return CategoryResult.builder()
                    .categoryId(id)
                    .categoryName(name)
                    .productCount(0)
                    .status("FAILED")
                    .error(error)
                    .build();
        }
    }
}