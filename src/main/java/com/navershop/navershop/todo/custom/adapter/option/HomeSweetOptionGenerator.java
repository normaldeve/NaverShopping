package com.navershop.navershop.todo.custom.adapter.option;

/**
 * HomeSweet 프로젝트의 OptionGenerator 구현
 */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class HomeSweetOptionGenerator implements OptionGenerator<Product> {
//
//    private final ProductOptionGenerator productOptionGenerator;
//    private final ProductRepository productRepository;
//
//    @Override
//    public void generateAndAddOptions(Product product, String categoryName) {
//        try {
//            // 1. 옵션 생성
//            ProductOptionGenerator.GeneratedProductOptions generatedOptions =
//                    productOptionGenerator.generateOptions(categoryName);
//
//            // 2. 옵션 그룹 추가
//            if (generatedOptions.getOptionGroups() != null && !generatedOptions.getOptionGroups().isEmpty()) {
//                for (ProductOptionGroup optionGroup : generatedOptions.getOptionGroups()) {
//                    product.addOption(optionGroup);
//                }
//            }
//
//            // 3. SKU 추가
//            if (generatedOptions.getSkuInfos() != null && !generatedOptions.getSkuInfos().isEmpty()) {
//                for (ProductOptionGenerator.SkuWithOptionInfo skuInfo : generatedOptions.getSkuInfos()) {
//                    Sku sku = skuInfo.getSku();
//                    product.addSku(sku);
//
//                    // flush로 SKU ID 생성
//                    productRepository.flush();
//
//                    // SKU에 옵션 연결
//                    for (ProductOptionValue optionValue : skuInfo.getOptionValues()) {
//                        ProductSkuOption skuOption = ProductSkuOption.builder()
//                                .sku(sku)
//                                .optionValue(optionValue)
//                                .build();
//                        sku.addSkuOption(skuOption);
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("옵션 생성 실패: {}", product.getName(), e);
//        }
//    }
//
//    @Override
//    public boolean needsOptions(String categoryName) {
//        // 모든 카테고리에 옵션 추가
//        return true;
//    }
//}
