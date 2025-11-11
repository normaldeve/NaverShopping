package com.navershop.navershop.template.service;

import com.navershop.navershop.config.MemoryMonitor;
import com.navershop.navershop.core.api.NaverShoppingApiClient;
import com.navershop.navershop.core.dto.NaverShoppingResponse;
import com.navershop.navershop.template.adapter.mapper.ProductMapper;
import com.navershop.navershop.template.adapter.option.OptionGenerator;
import com.navershop.navershop.template.adapter.provider.category.CategoryProvider;
import com.navershop.navershop.template.adapter.provider.product.ProductProvider;
import com.navershop.navershop.template.adapter.provider.user.UserProvider;
import com.navershop.navershop.todo.custom.adapter.naming.ProductNameFactory;
import com.navershop.navershop.todo.custom.adapter.option.BrandCatalog;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    protected final ProductNameFactory productNameFactory;
    protected final ProductBatchSaveService<PRODUCT> productProductBatchSaveService;

    protected BaseCrawlingService(
            NaverShoppingApiClient apiClient,
            ProductMapper<PRODUCT, CATEGORY, USER> productMapper,
            ProductProvider<PRODUCT> productProvider,
            CategoryProvider<CATEGORY> categoryProvider,
            UserProvider<USER> userProvider,
            OptionGenerator<PRODUCT> optionGenerator,
            ProductNameFactory productNameFactory,
            ProductBatchSaveService<PRODUCT> productProductBatchSaveService) {
        this.apiClient = apiClient;
        this.productMapper = productMapper;
        this.productProvider = productProvider;
        this.categoryProvider = categoryProvider;
        this.userProvider = userProvider;
        this.optionGenerator = optionGenerator;
        this.productNameFactory = productNameFactory;
        this.productProductBatchSaveService = productProductBatchSaveService;
    }

    // 배치 크기 설정
    private static final int BRAND_BATCH_SIZE = 10;        // 브랜드 5개씩 처리
    private static final int PRODUCT_BATCH_SIZE = 100;     // 상품 50개씩 저장
    private static final int PRODUCT_NAME_BATCH_SIZE = 100; // 상품명 100개씩 처리

    // 메모리 임계값
    private static final double MEMORY_WARNING_THRESHOLD = 0.85;  // 85% 경고
    private static final double MEMORY_DANGER_THRESHOLD = 0.90;   // 90% 위험

    /**
     * 스트리밍 방식의 크롤링 (메모리 모니터링 포함)
     */
    public CrawlingResult crawlAllCategoriesStreaming(Long userId, int productsPerCategory) {
        log.info("===== 🚀 스트리밍 크롤링 시작 =====");
        MemoryMonitor.logMemoryUsage("시작");

        long startTime = System.currentTimeMillis();

        USER adminUser = userProvider.findById(userId);
        List<CATEGORY> targetCategories = findLeafCategories();
        log.info("검색 대상 카테고리 수: {}", targetCategories.size());

        int totalProducts = 0;
        int successCategories = 0;
        int failedCategories = 0;
        List<CategoryResult> categoryResults = new ArrayList<>();

        // 카테고리별 순차 처리
        for (int i = 0; i < targetCategories.size(); i++) {
            CATEGORY category = targetCategories.get(i);
            String categoryName = categoryProvider.getCategoryName(category);
            Long categoryId = categoryProvider.getCategoryId(category);

            log.info("📦 [{}/{}] 카테고리 '{}' 처리 중...",
                    i + 1, targetCategories.size(), categoryName);

            try {
                // 메모리 체크 (위험 수준이면 잠시 대기)
                if (MemoryMonitor.isMemoryDanger(MEMORY_DANGER_THRESHOLD)) {
                    log.warn("⚠️ 메모리 사용률 90% 초과! 3초 대기 후 GC 수행");
                    Thread.sleep(3000);
                    MemoryMonitor.requestGC();
                }

                int savedCount = crawlAndSaveByCategoryStreaming(category, adminUser, productsPerCategory);

                if (savedCount > 0) {
                    categoryResults.add(CategoryResult.success(categoryId, categoryName, savedCount));
                    totalProducts += savedCount;
                    successCategories++;
                    log.info("✅ 카테고리 '{}' 완료: {}개 저장 (누적: {}개)",
                            categoryName, savedCount, totalProducts);
                } else {
                    categoryResults.add(CategoryResult.noResults(categoryId, categoryName));
                    log.warn("⚠️ 카테고리 '{}'에서 검색 결과 없음", categoryName);
                }

            } catch (Exception e) {
                log.error("❌ 카테고리 '{}' 크롤링 실패: {}", categoryName, e.getMessage(), e);
                categoryResults.add(CategoryResult.failed(categoryId, categoryName, e.getMessage()));
                failedCategories++;
            }

            // 5개 카테고리마다 메모리 모니터링
            if ((i + 1) % 5 == 0) {
                MemoryMonitor.monitorAndCleanIfNeeded("카테고리 " + (i + 1) + "개 완료");
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        log.info("===== ✅ 스트리밍 크롤링 완료 =====");
        log.info("총 카테고리: {}, 성공: {}, 실패: {}, 총 상품: {}, 소요시간: {}초",
                targetCategories.size(), successCategories, failedCategories,
                totalProducts, duration);

        MemoryMonitor.logMemoryUsage("완료");

        return CrawlingResult.builder()
                .totalCategories(targetCategories.size())
                .successCategories(successCategories)
                .failedCategories(failedCategories)
                .totalProducts(totalProducts)
                .durationSeconds(duration)
                .categoryResults(categoryResults)
                .build();
    }

    /**
     * 카테고리별 스트리밍 처리
     */
    protected int crawlAndSaveByCategoryStreaming(CATEGORY category, USER seller, int count) {
        String categoryName = categoryProvider.getCategoryName(category);
        String keyword = buildFullCategoryPath(category);

        log.info("🔍 검색 키워드: '{}' (카테고리: '{}')", keyword, categoryName);

        // API 호출 (템플릿 1개만 가져오기)
        NaverShoppingResponse response = apiClient.searchMultiplePagesReactive(keyword, 1, 100, "sim");

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            log.warn("검색 결과 없음: {}", categoryName);
            return 0;
        }

        NaverShoppingResponse.NaverShoppingItem sourceItem = response.getItems().get(0);
        BrandCatalog catalog = BrandCatalog.fromCategoryName(categoryName);
        List<String> allBrands = catalog.getBrands();

        log.info("📊 브랜드 {}개를 {}개씩 배치 처리", allBrands.size(), BRAND_BATCH_SIZE);

        int totalSaved = 0;

        // 브랜드를 배치로 나누어 처리
        for (int brandBatchStart = 0; brandBatchStart < allBrands.size(); brandBatchStart += BRAND_BATCH_SIZE) {
            int brandBatchEnd = Math.min(brandBatchStart + BRAND_BATCH_SIZE, allBrands.size());
            List<String> brandBatch = allBrands.subList(brandBatchStart, brandBatchEnd);

            log.info("🏷️ 브랜드 배치 {}-{}/{} 처리 중...",
                    brandBatchStart + 1, brandBatchEnd, allBrands.size());

            // 메모리 경고 수준이면 GC
            if (MemoryMonitor.isMemoryDanger(MEMORY_WARNING_THRESHOLD)) {
                MemoryMonitor.monitorAndCleanIfNeeded("브랜드 배치 " + (brandBatchStart / BRAND_BATCH_SIZE + 1));
            }

            int batchSaved = processBrandBatch(brandBatch, sourceItem, category, seller, categoryName);
            totalSaved += batchSaved;

            log.info("💾 브랜드 배치 저장 완료: {}개 (누적: {}개)", batchSaved, totalSaved);
        }

        return totalSaved;
    }

    /**
     * 브랜드 배치 처리
     */
    private int processBrandBatch(
            List<String> brandBatch,
            NaverShoppingResponse.NaverShoppingItem sourceItem,
            CATEGORY category,
            USER seller,
            String categoryName) {

        List<PRODUCT> productBuffer = new ArrayList<>(PRODUCT_BATCH_SIZE);
        int totalSaved = 0;

        for (String brand : brandBatch) {
            List<String> productNames = productNameFactory.generateAllCombinations(brand, categoryName);
            log.info("   📝 브랜드 '{}': {}개 상품명 생성", brand, productNames.size());

            // 상품명을 배치로 나누어 처리
            for (int nameStart = 0; nameStart < productNames.size(); nameStart += PRODUCT_NAME_BATCH_SIZE) {
                int nameEnd = Math.min(nameStart + PRODUCT_NAME_BATCH_SIZE, productNames.size());
                List<String> nameBatch = productNames.subList(nameStart, nameEnd);

                for (String productName : nameBatch) {
                    PRODUCT product = productMapper.map(sourceItem, category, seller, brand, productName);

                    if (optionGenerator != null && optionGenerator.needsOptions(categoryName)) {
                        optionGenerator.generateAndAddOptions(product, categoryName);
                    }

                    productBuffer.add(product);

                    // 버퍼가 가득 찼으면 저장
                    if (productBuffer.size() >= PRODUCT_BATCH_SIZE) {
                        int saved = productProductBatchSaveService.saveAndClearBuffer(productBuffer);
                        totalSaved += saved;
                    }
                }
            }
        }

        // 남은 상품 저장
        if (!productBuffer.isEmpty()) {
            int saved = productProductBatchSaveService.saveAndClearBuffer(productBuffer);
            totalSaved += saved;
        }

        return totalSaved;
    }


//    /**
//     * 카테고리별 크롤링 (Reactive 방식) - 모든 조합 생성 버전
//     */
//    protected int crawlAndSaveByCategoryReactive(CATEGORY category, USER seller, int count) {
//        String categoryName = categoryProvider.getCategoryName(category);
//        String keyword = buildFullCategoryPath(category);
//
//        log.info("검색 키워드: '{}' (카테고리: '{}')", keyword, categoryName);
//
//        int display = Math.min(count, 100);
//
//        // 🚀 Reactive 방식으로 API 호출 (1개만 가져오기)
//        NaverShoppingResponse response = apiClient.searchMultiplePagesReactive(
//                keyword, 1, display, "sim"); // ← count를 1로 변경
//
//        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
//            return 0;
//        }
//
//        // ✨ 모든 브랜드 조합 생성 (변경된 부분)
//        NaverShoppingResponse.NaverShoppingItem sourceItem = response.getItems().get(0);
//        List<PRODUCT> products = generateAllBrandCombinations(sourceItem, category, seller, categoryName);
//
//        log.info("{}개 상품 변환 완료 (모든 브랜드 조합)", products.size());
//
//        // 배치 저장
//        return saveProductsBatch(products);
//    }

//    /**
//     * 배치 저장
//     */
//    protected int saveProductsBatch(List<PRODUCT> products) {
//        if (products.isEmpty()) {
//            return 0;
//        }
//
//        log.info("💾 배치 저장 중... ({}개)", products.size());
//
//        // 배치 저장
//        int savedCount = 0;
//        int batchSize = 100;
//
//        for (int i = 0; i < products.size(); i += batchSize) {
//            int end = Math.min(i + batchSize, products.size());
//            List<PRODUCT> batch = products.subList(i, end);
//
//            try {
////                for (PRODUCT product : batch) {
////                    productProvider.save(product);
////                    savedCount++;
////                }
//                productProvider.saveAll(batch);
//                log.info("배치 저장 완료: {}-{}", i, end);
//            } catch (Exception e) {
//                log.error("배치 저장 실패: {}-{}", i, end, e);
//            }
//        }
//
//        return savedCount;
//    }

//    /**
//     * 모든 브랜드 × 소재 × 사이즈 조합 생성
//     */
//    private List<PRODUCT> generateAllBrandCombinations(
//            NaverShoppingResponse.NaverShoppingItem sourceItem,
//            CATEGORY category,
//            USER seller,
//            String categoryName) {
//
//        List<PRODUCT> products = new ArrayList<>();
//
//        // BrandCatalog에서 모든 브랜드 가져오기
//        BrandCatalog catalog = BrandCatalog.fromCategoryName(categoryName);
//        List<String> allBrands = catalog.getBrands();
//
//        log.info("브랜드 {}개로 조합 생성 시작", allBrands.size());
//
//        for (String brand : allBrands) {
//            // 해당 브랜드로 모든 조합의 상품명 생성
//            List<String> productNames = productNameFactory.generateAllCombinations(brand, categoryName);
//
//            for (String productName : productNames) {
//                // 커스텀 브랜드와 상품명으로 Product 생성
//                PRODUCT product = productMapper.map(sourceItem, category, seller, brand, productName);
//
//                // 옵션 생성
//                if (optionGenerator != null && optionGenerator.needsOptions(categoryName)) {
//                    optionGenerator.generateAndAddOptions(product, categoryName);
//                }
//
//                products.add(product);
//            }
//        }
//
//        log.info("총 {}개 상품 조합 생성 완료", products.size());
//        return products;
//    }

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
                .toList();

        log.info("전체 카테고리: {}개, 리프 카테고리: {}개",
                allCategories.size(), leafCategories.size());
        return leafCategories;
    }

    /**
     * 전체 카테고리 경로 중 2레벨~3레벨만 키워드로 사용
     */
    protected String buildFullCategoryPath(CATEGORY category) {
        List<String> pathNames = new ArrayList<>();
        CATEGORY current = category;

        // 상위까지 역순으로 추적
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

        // depth 기준으로 reverse 정렬: [1레벨, 2레벨, 3레벨]
        Collections.reverse(pathNames);

        // 2~3레벨만 남기기
        if (pathNames.size() >= 3) {
            // 0: 최상위, 1: 2레벨, 2: 3레벨
            pathNames = pathNames.subList(1, 3);
        } else if (pathNames.size() == 2) {
            // 1~2레벨만 존재하면 그대로
            pathNames = pathNames.subList(1, 2);
        } else {
            // 루트 하나만 있으면 그대로 유지
        }

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