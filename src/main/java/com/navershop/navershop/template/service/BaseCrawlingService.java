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
        List<CATEGORY> targetCategories = findSearchTargetCategories();
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
                            categoryProvider.getCategoryDepth(category),
                            savedCount
                    ));

                    totalProducts += savedCount;
                    successCategories++;

                    log.info("카테고리 '{}' 완료: {}개 저장", categoryName, savedCount);
                } else {
                    categoryResults.add(CategoryResult.noResults(
                            categoryProvider.getCategoryId(category),
                            categoryName,
                            categoryProvider.getCategoryDepth(category)
                    ));

                    log.warn("카테고리 '{}'에서 검색 결과 없음", categoryName);
                }

                Thread.sleep(200);

            } catch (Exception e) {
                log.error("카테고리 '{}' 크롤링 실패: {}", categoryName, e.getMessage(), e);

                categoryResults.add(CategoryResult.failed(
                        categoryProvider.getCategoryId(category),
                        categoryName,
                        categoryProvider.getCategoryDepth(category),
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
        String keyword = sanitizeKeyword(categoryName);

        log.info("검색 키워드: '{}' (원본: '{}')", keyword, categoryName);

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

    protected List<CATEGORY> findSearchTargetCategories() {
        List<CATEGORY> allCategories = categoryProvider.findAllCategories();
        List<CATEGORY> targetCategories = new ArrayList<>();

        Map<Long, List<CATEGORY>> categoriesByParent = new HashMap<>();
        List<CATEGORY> depth1Categories = new ArrayList<>();

        for (CATEGORY category : allCategories) {
            Integer depth = categoryProvider.getCategoryDepth(category);

            if (depth == 1) {
                depth1Categories.add(category);
            } else if (depth == 2) {
                Long parentId = categoryProvider.getParentCategoryId(category);
                categoriesByParent.computeIfAbsent(parentId, k -> new ArrayList<>())
                        .add(category);
            }
        }

        for (CATEGORY depth1 : depth1Categories) {
            Long categoryId = categoryProvider.getCategoryId(depth1);
            List<CATEGORY> depth2List = categoriesByParent.get(categoryId);

            if (depth2List != null && !depth2List.isEmpty()) {
                targetCategories.addAll(depth2List);
            } else {
                targetCategories.add(depth1);
            }
        }

        return targetCategories;
    }

    protected String sanitizeKeyword(String keyword) {
        if (keyword == null) return "";

        return keyword
                .replace("+", " ")
                .replace("·", " ")
                .replace("、", " ")
                .replace("，", " ")
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
        private Integer depth;
        private Integer productCount;
        private String status;
        private String error;

        public static CategoryResult success(Long id, String name, Integer depth, Integer count) {
            return CategoryResult.builder()
                    .categoryId(id)
                    .categoryName(name)
                    .depth(depth)
                    .productCount(count)
                    .status("SUCCESS")
                    .build();
        }

        public static CategoryResult noResults(Long id, String name, Integer depth) {
            return CategoryResult.builder()
                    .categoryId(id)
                    .categoryName(name)
                    .depth(depth)
                    .productCount(0)
                    .status("NO_RESULTS")
                    .build();
        }

        public static CategoryResult failed(Long id, String name, Integer depth, String error) {
            return CategoryResult.builder()
                    .categoryId(id)
                    .categoryName(name)
                    .depth(depth)
                    .productCount(0)
                    .status("FAILED")
                    .error(error)
                    .build();
        }
    }
}