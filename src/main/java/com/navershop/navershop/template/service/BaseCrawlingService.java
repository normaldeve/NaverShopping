package com.navershop.navershop.template.service;

import com.navershop.navershop.template.adapter.provider.category.CategoryProvider;
import com.navershop.navershop.template.adapter.mapper.ProductMapper;
import com.navershop.navershop.template.adapter.option.OptionGenerator;
import com.navershop.navershop.template.adapter.provider.product.ProductProvider;
import com.navershop.navershop.template.adapter.provider.user.UserProvider;
import com.navershop.navershop.core.api.NaverShoppingApiClient;
import com.navershop.navershop.core.dto.NaverShoppingResponse;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 범용 크롤링 서비스 추상 클래스 (Core - 수정 금지)
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

    @Transactional
    public CrawlingResult crawlAllCategories(Long userId, int productsPerCategory) {
        log.info("===== 전체 카테고리 크롤링 시작 =====");

        USER adminUser = userProvider.findById(userId);
        List<CATEGORY> targetCategories = findLeafCategories();
        log.info("검색 대상 카테고리 수: {}", targetCategories.size());

        int totalProducts = 0;
        int successCategories = 0;
        int failedCategories = 0;

        List<CategoryResult> categoryResults = new ArrayList<>();

        for (CATEGORY category : targetCategories) {
            String categoryName = categoryProvider.getCategoryName(category);

            try {
                log.info("카테고리 '{}' 크롤링 시작...", categoryName);

                int savedCount = crawlAndSaveByCategory(
                        category, adminUser, productsPerCategory
                );

                if (savedCount > 0) {
                    categoryResults.add(CategoryResult.success(
                            categoryProvider.getCategoryId(category),
                            categoryName,
                            savedCount
                    ));

                    totalProducts += savedCount;
                    successCategories++;

                    log.info("카테고리 '{}' 완료: {}개 저장", categoryName, savedCount);
                } else {
                    categoryResults.add(CategoryResult.noResults(
                            categoryProvider.getCategoryId(category),
                            categoryName
                    ));

                    log.warn("카테고리 '{}'에서 검색 결과 없음", categoryName);
                }

                Thread.sleep(200);

            } catch (Exception e) {
                log.error("카테고리 '{}' 크롤링 실패: {}", categoryName, e.getMessage(), e);

                categoryResults.add(CategoryResult.failed(
                        categoryProvider.getCategoryId(category),
                        categoryName,
                        e.getMessage()
                ));

                failedCategories++;
            }
        }

        log.info("===== 전체 카테고리 크롤링 완료 =====");
        log.info("총 카테고리: {}, 성공: {}, 실패: {}, 총 상품: {}",
                targetCategories.size(), successCategories, failedCategories, totalProducts);

        return CrawlingResult.builder()
                .totalCategories(targetCategories.size())
                .successCategories(successCategories)
                .failedCategories(failedCategories)
                .totalProducts(totalProducts)
                .categoryResults(categoryResults)
                .build();
    }

    protected int crawlAndSaveByCategory(CATEGORY category, USER seller, int count) {
        String categoryName = categoryProvider.getCategoryName(category);

        // 🔥 상위 카테고리를 포함한 전체 경로로 검색 키워드 생성
        String keyword = buildFullCategoryPath(category);

        log.info("검색 키워드: '{}' (카테고리: '{}')", keyword, categoryName);

        int display = Math.min(count, 100);
        NaverShoppingResponse response = apiClient.searchMultiplePages(keyword, count, display, "sim");

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            return 0;
        }

        List<PRODUCT> products = new ArrayList<>();
        int itemsToProcess = Math.min(response.getItems().size(), count);

        for (int i = 0; i < itemsToProcess; i++) {
            NaverShoppingResponse.NaverShoppingItem item = response.getItems().get(i);
            PRODUCT product = productMapper.map(item, category, seller);
            products.add(product);
        }

        log.info("{}개 상품 변환 완료", products.size());

        if (optionGenerator != null && optionGenerator.needsOptions(categoryName)) {
            log.info("🔧 옵션 생성 중...");
            for (PRODUCT product : products) {
                optionGenerator.generateAndAddOptions(product, categoryName);
            }
        }

        log.info("💾 상품 저장 중...");
        return productProvider.saveAll(products);
    }

    /**
     * 🔥 리프 노드(최하위) 카테고리만 조회
     *
     * 예시:
     * - 가구 (부모)
     *   - 침대 (자식) ← 선택됨
     *   - 소파 (자식) ← 선택됨
     * - 유아 (부모)
     *   - 침구 (자식) ← 선택됨
     */
    protected List<CATEGORY> findLeafCategories() {
        List<CATEGORY> allCategories = categoryProvider.findAllCategories();
        Set<Long> parentIds = new HashSet<>();

        // 1. 부모 카테고리 ID들을 모두 수집
        for (CATEGORY category : allCategories) {
            Long parentId = categoryProvider.getParentCategoryId(category);
            if (parentId != null) {
                parentIds.add(parentId);
            }
        }

        // 2. 자식이 없는 카테고리만 필터링 (리프 노드)
        List<CATEGORY> leafCategories = new ArrayList<>();
        for (CATEGORY category : allCategories) {
            Long categoryId = categoryProvider.getCategoryId(category);
            if (!parentIds.contains(categoryId)) {
                leafCategories.add(category);
            }
        }

        log.info("전체 카테고리: {}개, 리프 카테고리: {}개", allCategories.size(), leafCategories.size());
        return leafCategories;
    }

    /**
     * 🔥 상위 카테고리를 포함한 전체 경로 생성
     *
     * 예시:
     * - 유아 > 침구 → "유아 침구"
     * - 가구 > 침실 > 침대 → "가구 침실 침대"
     * - 주방용품 → "주방용품" (부모 없음)
     */
    protected String buildFullCategoryPath(CATEGORY category) {
        List<String> pathNames = new ArrayList<>();
        CATEGORY current = category;

        // 현재 카테고리부터 최상위 부모까지 역순으로 수집
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

        // 역순으로 수집했으므로 뒤집기 (최상위 부모 → 현재 카테고리 순서)
        Collections.reverse(pathNames);

        // 공백으로 연결하여 검색 키워드 생성
        String fullPath = String.join(" ", pathNames);

        // 특수문자 정리
        return sanitizeKeyword(fullPath);
    }

    protected String sanitizeKeyword(String keyword) {
        if (keyword == null) return "";

        return keyword
                .replace("+", " ")
                .replace("·", " ")
                .replace("、", " ")
                .replace("，", " ")
                .replaceAll("\\s+", " ")  // 연속된 공백을 하나로
                .trim();
    }

    @Data
    @Builder
    public static class CrawlingResult {
        private Integer totalCategories;
        private Integer successCategories;
        private Integer failedCategories;
        private Integer totalProducts;
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