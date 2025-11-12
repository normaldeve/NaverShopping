package com.navershop.navershop.todo.custom.adapter.naming.category.패브릭.토퍼;

import com.navershop.navershop.todo.custom.adapter.naming.ProductNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 토퍼 네이밍
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 10.
 */
@Slf4j
@Component
public class TopperNamingStrategy implements ProductNamingStrategy {

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

    // 부가기능
    private static final List<String> FEATURES = Arrays.asList(
            "알러지방지", "진드기방지", "양면사용",
            "미끄럼방지", "접이식", "커버분리가능", "커버패딩내장",
            "냉감", "바이오워싱", "커버세탁가능", "방수기능"
    );

    // 재질
    private static final List<String> MATERIALS = Arrays.asList(
            "천연라텍스", "플러시폼", "메모리폼", "쿨젤메모리폼", "거위털",
            "솜", "양모", "홀로파이버", "인조라텍스", "우레탄폼",
            "폴리에스터", "나일론"
    );

    // 커버
    private static final List<String> COVER = Arrays.asList(
            "오가닉코튼", "면", "순면", "아사면", "극세사",
            "모달", "리넨", "실크"
    );

    private static final List<String> COLORS = Arrays.asList(
            "화이트", "블랙", "오렌지", "그린", "골드",
            "브라운", "실버", "블루", "옐로우", "네이비"
    );

    @Override
    public String generateProductName(String brand, String categoryName) {
        String descriptor = getRandomItem(DESCRIPTORS);
        String size = getRandomItem(FEATURES);
        String material = getRandomItem(MATERIALS);

        return String.format("%s %s %s %s %s",
                brand, descriptor, size, material, categoryName);
    }

    @Override
    public boolean supports(String categoryName) {
        return categoryName != null && categoryName.contains("토퍼");
    }

    @Override
    public boolean supportsAllCombinations() {
        return true; // 모든 조합 생성 지원
    }

    @Override
    public List<String> generateAllCombinations(String brand, String categoryName) {
        List<String> allCombinations = new ArrayList<>();

        for (String feature : FEATURES) {
            for (String material : MATERIALS) {
                for (String cover : COVER) {
                    for (String color : COLORS) {
                        String descriptor = getRandomItem(DESCRIPTORS);

                        String productName = String.format("[%s] %s %s 재질의 %s %s %s (%s)",
                                brand, descriptor, material, color, cover, categoryName, feature);

                        allCombinations.add(productName);
                    }
                }
            }
        }

        log.info("소파 {}개 조합 생성",
                allCombinations.size());

        return allCombinations;
    }

    private String getRandomItem(List<String> items) {
        return items.get(RANDOM.nextInt(items.size()));
    }
}
