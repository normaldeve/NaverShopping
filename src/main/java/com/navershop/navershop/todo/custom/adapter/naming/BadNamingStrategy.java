package com.navershop.navershop.todo.custom.adapter.naming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 10.
 */
@Slf4j
@Component
public class BadNamingStrategy implements ProductNamingStrategy{

    private static final Random RANDOM = new Random();

    // 꾸미는말 (침대 분위기/스타일)
    private static final List<String> DESCRIPTORS = Arrays.asList(
            "호텔", "모던", "클래식", "북유럽", "빈티지",
            "심플", "럭셔리", "프리미엄", "컴팩트", "미니멀"
    );

    // 소재 (옵션1)
    private static final List<String> MATERIALS = Arrays.asList(
            "철제", "원목", "패브릭", "가죽", "벨벳",
            "메탈", "우드", "MDF", "합판", "무늬목"
    );

    // 사이즈 (옵션2) - CategoryOptionStrategy의 침대 사이즈와 매칭
    private static final List<String> SIZES = Arrays.asList(
            "싱글", "슈퍼싱글", "퀸", "킹", "더블"
    );

    @Override
    public String generateProductName(String brand, String categoryName) {
        String descriptor = getRandomItem(DESCRIPTORS);
        String material = getRandomItem(MATERIALS);
        String size = getRandomItem(SIZES);

        // [브랜드] [꾸미는말] [소재] [사이즈] [침대프레임]
        String productName = String.format("%s %s %s %s %s",
                brand, descriptor, material, size, categoryName);

        log.debug("생성된 침대프레임 상품명: {}", productName);
        return productName;
    }

    @Override
    public boolean supports(String categoryName) {
        return categoryName != null &&
                (categoryName.contains("침대프레임") ||
                        categoryName.equals("침대"));
    }

    private String getRandomItem(List<String> items) {
        return items.get(RANDOM.nextInt(items.size()));
    }
}
