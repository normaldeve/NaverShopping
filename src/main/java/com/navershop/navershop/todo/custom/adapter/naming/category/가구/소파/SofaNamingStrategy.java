package com.navershop.navershop.todo.custom.adapter.naming.category.가구.소파;

import com.navershop.navershop.todo.custom.adapter.naming.ProductNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 소파 카테고리 전용 네이밍 전략
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 10.
 */
@Slf4j
@Component
public class SofaNamingStrategy implements ProductNamingStrategy {

    private static final Random RANDOM = new Random();

    // 꾸미는말 - 랜덤
    private static final List<String> DESCRIPTORS = Arrays.asList(
            "모던", "클래식", "빈티지", "럭셔리", "컴팩트",
            "스칸디", "미니멀", "프리미엄", "유러피안", "심플"
    );

    // 인원 - 모든 조합
    private static final List<String> SIZES = Arrays.asList(
            "1인용", "2인용", "3인용", "4인용", "5인용", "6인용"
    );

    // 재질 - 모든 조합
    private static final List<String> MATERIALS = Arrays.asList(
            "패브릭", "천연가죽", "인조가죽", "벨벳", "린넨",
            "마이크로화이버", "스웨이드", "코듀로이", "샤무드", "아쿠아클린"
    );

    private static final List<String> SHAPES = Arrays.asList(
            "일자형", "카우치형", "코너형", "모듈형", "좌식형", "침대형"
    );

    // 쿠션감
    private static final List<String> FLUFFY = Arrays.asList(
            "푹신한", "약간 푹신한", "약간 하드한", "하드한"
    );

    @Override
    public String generateProductName(String brand, String categoryName) {
        String descriptor = getRandomItem(DESCRIPTORS);
        String size = getRandomItem(SIZES);
        String material = getRandomItem(MATERIALS);

        return String.format("%s %s %s %s %s",
                brand, descriptor, size, material, categoryName);
    }

    @Override
    public boolean supports(String categoryName) {
        return categoryName != null && categoryName.contains("소파");
    }

    @Override
    public boolean supportsAllCombinations() {
        return true; // 모든 조합 생성 지원
    }

    @Override
    public List<String> generateAllCombinations(String brand, String categoryName) {
        List<String> allCombinations = new ArrayList<>();

        for (String size : SIZES) {
            for (String material : MATERIALS) {
                for (String shape : SHAPES) {
                    for (String fluffy : FLUFFY) {
                        String descriptor = getRandomItem(DESCRIPTORS);

                        String productName = String.format("%s %s %s %s %s %s %s",
                                brand, descriptor, size, material, shape, fluffy, categoryName);

                        allCombinations.add(productName);
                    }
                }
            }
        }

        log.info("소파 {}개 조합 생성 (인원: {}개, 재질: {}개)",
                allCombinations.size(), SIZES.size(), MATERIALS.size());

        return allCombinations;
    }

    private String getRandomItem(List<String> items) {
        return items.get(RANDOM.nextInt(items.size()));
    }
}
