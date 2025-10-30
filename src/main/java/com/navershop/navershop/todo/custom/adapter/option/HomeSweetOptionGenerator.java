package com.navershop.navershop.todo.custom.adapter.option;

import com.navershop.navershop.todo.repository.product.Product;
import com.navershop.navershop.todo.repository.option.ProductOptionGroup;
import com.navershop.navershop.todo.repository.option.ProductOptionValue;
import com.navershop.navershop.todo.repository.sku.Sku;
import com.navershop.navershop.todo.repository.sku.ProductSkuOption;
import com.navershop.navershop.template.adapter.option.OptionGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * HomeSweet 프로젝트의 OptionGenerator 구현
 *
 * 카테고리별로 자동으로 옵션 그룹과 SKU를 생성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HomeSweetOptionGenerator implements OptionGenerator<Product> {

    private final CategoryOptionStrategy optionStrategy;

    @Override
    public void generateAndAddOptions(Product product, String categoryName) {
        try {
            log.info("=== 옵션 생성 시작: {} ===", product.getName());

            // 1. 카테고리에 맞는 옵션 설정 가져오기
            CategoryOptionStrategy.CategoryOptionConfig config = optionStrategy.getConfig(categoryName);

            // 2. 옵션 그룹 생성 및 추가
            List<ProductOptionGroup> createdGroups = createAndAddOptionGroups(product, config);
            log.info("옵션 그룹 생성 완료: {}개", createdGroups.size());

            // 3. SKU 생성 및 추가
            int skuCount = createAndAddSkus(product, config, createdGroups);
            log.info("SKU 생성 완료: {}개", skuCount);

            log.info("=== 옵션 생성 완료: {} ===", product.getName());

        } catch (Exception e) {
            log.error("옵션 생성 실패: {}", product.getName(), e);
        }
    }

    @Override
    public boolean needsOptions(String categoryName) {
        return optionStrategy.needsOptions(categoryName);
    }

    /**
     * 옵션 그룹 생성 및 Product에 추가
     */
    private List<ProductOptionGroup> createAndAddOptionGroups(
            Product product,
            CategoryOptionStrategy.CategoryOptionConfig config) {

        List<ProductOptionGroup> createdGroups = new ArrayList<>();

        for (CategoryOptionStrategy.OptionGroupConfig groupConfig : config.getOptionGroups()) {
            // 옵션 값들 생성
            List<ProductOptionValue> optionValues = new ArrayList<>();
            for (CategoryOptionStrategy.OptionValueConfig valueConfig : groupConfig.getValues()) {
                ProductOptionValue optionValue = ProductOptionValue.builder()
                        .value(valueConfig.getValue())
                        .build();
                optionValues.add(optionValue);
            }

            // 옵션 그룹 생성
            ProductOptionGroup optionGroup = ProductOptionGroup.builder()
                    .groupName(groupConfig.getGroupName())
                    .values(optionValues)
                    .build();

            // Product에 추가
            product.addOption(optionGroup);
            createdGroups.add(optionGroup);
        }

        return createdGroups;
    }

    /**
     * SKU 생성 및 Product에 추가 (모든 옵션 조합)
     */
    private int createAndAddSkus(
            Product product,
            CategoryOptionStrategy.CategoryOptionConfig config,
            List<ProductOptionGroup> optionGroups) {

        // 각 옵션 그룹의 값들을 리스트로 준비
        List<List<OptionValueWithConfig>> optionValueLists = new ArrayList<>();

        for (int groupIdx = 0; groupIdx < config.getOptionGroups().size(); groupIdx++) {
            CategoryOptionStrategy.OptionGroupConfig groupConfig =
                    config.getOptionGroups().get(groupIdx);
            ProductOptionGroup createdGroup = optionGroups.get(groupIdx);

            List<OptionValueWithConfig> valueList = new ArrayList<>();
            for (int valueIdx = 0; valueIdx < groupConfig.getValues().size(); valueIdx++) {
                CategoryOptionStrategy.OptionValueConfig valueConfig =
                        groupConfig.getValues().get(valueIdx);
                ProductOptionValue createdValue =
                        createdGroup.getValues().get(valueIdx);

                valueList.add(new OptionValueWithConfig(createdValue, valueConfig));
            }
            optionValueLists.add(valueList);
        }

        // 모든 옵션 조합 생성 (카테시안 곱)
        List<List<OptionValueWithConfig>> combinations = cartesianProduct(optionValueLists);
        log.info("총 {}개의 SKU 조합 생성", combinations.size());

        // 각 조합에 대해 SKU 생성 및 추가
        for (List<OptionValueWithConfig> combination : combinations) {
            // SKU 정보 계산
            SkuInfo skuInfo = calculateSkuInfo(combination);

            // SKU 생성
            Sku sku = Sku.builder()
                    .priceAdjustment(skuInfo.priceAdjustment())
                    .stockQuantity(skuInfo.stock())
                    .build();

            // Product에 SKU 추가
            product.addSku(sku);

            // SKU에 옵션 값 연결
            for (OptionValueWithConfig valueWithConfig : combination) {
                ProductSkuOption skuOption = ProductSkuOption.builder()
                        .sku(sku)
                        .optionValue(valueWithConfig.optionValue())
                        .build();
                sku.addSkuOption(skuOption);
            }
        }

        return combinations.size();
    }

    /**
     * SKU 정보 계산 (가격 조정 + 재고)
     */
    private SkuInfo calculateSkuInfo(List<OptionValueWithConfig> combination) {
        int totalPriceAdjustment = 0;
        long minStock = Long.MAX_VALUE;

        for (OptionValueWithConfig valueWithConfig : combination) {
            totalPriceAdjustment += valueWithConfig.config().getPriceAdjustment();
            minStock = Math.min(minStock, valueWithConfig.config().getStock());
        }

        return new SkuInfo(totalPriceAdjustment, minStock);
    }

    /**
     * 카테시안 곱 계산 (모든 조합 생성)
     */
    private <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> result = new ArrayList<>();
        cartesianProductHelper(lists, 0, new ArrayList<>(), result);
        return result;
    }

    private <T> void cartesianProductHelper(
            List<List<T>> lists,
            int depth,
            List<T> current,
            List<List<T>> result) {

        if (depth == lists.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (T item : lists.get(depth)) {
            current.add(item);
            cartesianProductHelper(lists, depth + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * 옵션 값과 설정을 함께 담는 레코드
     */
    private record OptionValueWithConfig(
            ProductOptionValue optionValue,
            CategoryOptionStrategy.OptionValueConfig config
    ) {}

    /**
     * SKU 정보 DTO
     */
    private record SkuInfo(int priceAdjustment, long stock) {}
}