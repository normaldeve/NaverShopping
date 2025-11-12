package com.navershop.navershop.todo.custom.adapter.naming.category.패브릭.러그;

import com.navershop.navershop.todo.custom.adapter.naming.ProductNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 테이블 이름 짓기
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 11.
 */
@Slf4j
@Component
public class RugNamingStrategy implements ProductNamingStrategy {


    private static final Random RANDOM = new Random();

    // 꾸미는말 - 랜덤
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

    // 재질 - 모든 조합
    private static final List<String> DETAILS = Arrays.asList(
            "무지", "체크", "스트라이프", "프린트", "에스닉",
            "헤링본", "기하학", "캐릭터", "플라워"
    );

    private static final List<String> SHAPES = Arrays.asList(
            "사각형", "직사각형", "원형", "타원형", "정사각형",
            "반원형"
    );

    private static final List<String> FEATURES = Arrays.asList(
            "단모", "천연양모", "극세사", "소프트", "인테리어", "먼지없는", "사계절"
    );

    // 쿠션감
    private static final List<String> POSSIBLE = Arrays.asList(
            "세탁가능", "손세탁", "드라이클리닝", "방수"
    );

    @Override
    public String generateProductName(String brand, String categoryName) {
        String descriptor = getRandomItem(DESCRIPTORS);
        String material = getRandomItem(DETAILS);

        return String.format("%s %s %s %s",
                brand, descriptor, material, categoryName);
    }

    @Override
    public boolean supports(String categoryName) {
        return categoryName != null && categoryName.contains("러그");
    }

    @Override
    public boolean supportsAllCombinations() {
        return true; // 모든 조합 생성 지원
    }

    @Override
    public List<String> generateAllCombinations(String brand, String categoryName) {
        List<String> allCombinations = new ArrayList<>();

        for (String detail : DETAILS) {
            for (String shape : SHAPES) {
                for (String feature : FEATURES) {
                    for (String possible : POSSIBLE) {
                        String descriptor = getRandomItem(DESCRIPTORS);

                        String productName = String.format("[%s] %s %s %s %s %s (%s)",
                                brand, descriptor, detail, shape, feature, categoryName, possible);

                        allCombinations.add(productName);
                    }
                }
            }
        }

        log.info("러그 {}개 조합 생성 (인원: {}개, 재질: {}개)",
                allCombinations.size(), SHAPES.size(), POSSIBLE.size());

        return allCombinations;
    }

    private String getRandomItem(List<String> items) {
        return items.get(RANDOM.nextInt(items.size()));
    }
}
