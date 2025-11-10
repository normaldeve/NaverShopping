package com.navershop.navershop.todo.custom.adapter.naming;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 상품명 생성 팩토리
 *
 * 카테고리에 맞는 네이밍 전략을 선택하여 상품명 생성
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 10.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductNameFactory {

    private static final Random RANDOM = new Random();
    private final List<ProductNamingStrategy> namingStrategies;

    // 기본 꾸미는말 (특정 전략이 없을 때 사용)
    private static final List<String> DEFAULT_DESCRIPTORS = List.of(
            "프리미엄", "고급", "신상", "베스트", "인기",
            "특가", "추천", "스타일", "트렌디", "모던"
    );

    /**
     * 카테고리에 맞는 상품명 생성
     *
     * @param brand 브랜드명
     * @param categoryName 카테고리명
     * @return 생성된 상품명
     */
    public String generateProductName(String brand, String categoryName) {
        // 1. 카테고리에 맞는 전략 찾기
        ProductNamingStrategy strategy = findStrategy(categoryName);

        if (strategy != null) {
            return strategy.generateProductName(brand, categoryName);
        }

        // 2. 기본 전략 사용
        return generateDefaultName(brand, categoryName);
    }

    /**
     * 카테고리에 맞는 전략 찾기
     */
    private ProductNamingStrategy findStrategy(String categoryName) {
        return namingStrategies.stream()
                .filter(strategy -> strategy.supports(categoryName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 기본 상품명 생성 (전략이 없는 경우)
     * 형식: [브랜드] [꾸미는말] [카테고리]
     */
    private String generateDefaultName(String brand, String categoryName) {
        String descriptor = DEFAULT_DESCRIPTORS.get(RANDOM.nextInt(DEFAULT_DESCRIPTORS.size()));
        String productName = String.format("%s %s %s", brand, descriptor, categoryName);

        log.debug("기본 전략으로 생성된 상품명: {}", productName);
        return productName;
    }
}
