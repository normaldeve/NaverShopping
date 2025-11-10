package com.navershop.navershop.todo.custom.adapter.naming.category.가구.침대;

import com.navershop.navershop.todo.custom.adapter.naming.ProductNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
public class BadNamingStrategy implements ProductNamingStrategy {

    private static final Random RANDOM = new Random();

    // 꾸미는말 (침대 분위기/스타일)
    private static final List<String> DESCRIPTORS = Arrays.asList(
            "편안한 휴식이 되는", "포근함이 느껴지는", "공간을 채워주는", "세련된 분위기의", "감각적인 디자인의",
            "따뜻한 느낌의", "모던한 감성의", "심플한 매력의", "고급스러움을 담은", "아늑한 무드의",
            "트렌디한 감각의", "집 안의 포인트가 되는", "미니멀한 감성의", "부드러운 착석감의", "안락함이 돋보이는",
            "자연스러움을 담은", "고요한 휴식의", "모던한 감각의", "편안함이 스며든", "감성적인 무드의",
            "여유로움을 담은", "따뜻한 온기의", "세련된 컬러감의", "모던한 라인의", "조화로운 공간의",
            "심플하면서도 고급스러운", "부드럽게 감싸주는", "포근하게 안기는", "세련되게 완성된", "감성적인 공간을 만드는",
            "편안하게 기대어 쉬는", "은은한 매력의", "고급스러운 질감의", "트렌디하게 연출된", "포근한 감촉의",
            "여유로운 휴식을 위한", "감각적으로 디자인된", "공간의 품격을 높이는", "따뜻함이 머무는", "자연스러운 조화를 이룬",
            "감성적인 인테리어의", "부드럽게 이어지는", "여유로운 라인의", "모던하고 깔끔한", "안락한 공간을 완성하는",
            "감각적인 컬러의", "심플하고 세련된", "편안함을 선사하는", "부드러움이 살아있는", "따뜻함이 전해지는"
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

    private static final List<String> FRAMES = Arrays.asList(
            "평상형", "하단수납형", "하단오픈형", "저상형", "하단밀폐형",
            "무헤드형", "매트리스일체형", "매트매립형", "데이베드형", "블박이형",
            "사이드확장형"
    );

    private static final List<String> TYPES = Arrays.asList(
            "일반", "수납", "저상형", "패밀리", "이층", "벙커", "모션", "돌", "접이식"
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
        return categoryName != null && (categoryName.contains("침대프레임"));
    }

    @Override
    public boolean supportsAllCombinations() {
        return true;
    }

    @Override
    public List<String> generateAllCombinations(String brand, String categoryName) {
        List<String> allCombinations = new ArrayList<>();

        for (String material : MATERIALS) {
            for (String size : SIZES) {
                for (String frame : FRAMES) {
                    for (String type : TYPES) {
                        // 꾸미는말만 랜덤
                        String descriptor = getRandomItem(DESCRIPTORS);

                        String productName = String.format("%s %s %s %s사이즈 %s %s%s",
                                brand, descriptor, material, size, frame, type, categoryName);

                        allCombinations.add(productName);
                    }
                }
            }
        }

        log.info("총 {}개 조합 생성 (브랜드: {}, 소재: {}개, 사이즈: {}개)",
                allCombinations.size(), brand, MATERIALS.size(), SIZES.size());

        return allCombinations;
    }

    private String getRandomItem(List<String> items) {
        return items.get(RANDOM.nextInt(items.size()));
    }
}
