package com.navershop.navershop.todo.custom.adapter.naming.category.조명.천장등;

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
public class CeilingNamingStrategy implements ProductNamingStrategy {

    private static final Random RANDOM = new Random();

    // 꾸미는말 - 랜덤
    private static final List<String> DESCRIPTORS = Arrays.asList(
            "은은한 빛으로 공간을 채우는", "따뜻한 조명이 머무는", "감성적인 무드를 더하는", "아늑한 빛으로 물들이는", "부드러운 조명으로 완성된",
            "집 안의 포인트가 되는", "공간의 분위기를 바꾸는", "빛으로 감성을 표현하는", "편안한 휴식을 밝혀주는", "고요한 밤을 비추는",
            "따뜻한 색감의 조명으로", "감각적인 조도로 완성된", "트렌디한 조명 연출의", "무드를 밝혀주는", "차분한 분위기를 연출하는",
            "모던한 감성의 빛으로", "세련된 인테리어를 완성하는", "은은하게 스며드는 빛의", "공간의 품격을 높이는 조명", "빛으로 완성되는 감성 공간",
            "포근한 조명 아래에서", "따뜻한 분위기를 선사하는", "빛의 예술을 느낄 수 있는", "감성적인 조도를 지닌", "편안함이 느껴지는 조명",
            "세련된 컬러 조명으로", "빛의 농도로 감성을 표현하는", "고급스러운 빛의 질감을 담은", "하루의 끝을 밝혀주는", "차분한 빛의 온기를 머금은",
            "자연스러운 조도 변화로", "감각적인 라이트 포인트의", "공간을 밝혀주는 부드러운", "고요한 무드를 완성하는", "포근하게 감싸는 빛의",
            "트렌디하게 조화를 이루는", "은은한 조명 아래 완성되는", "감각적인 조명 인테리어의", "빛으로 편안함을 주는", "모던하고 따뜻한 조명의",
            "공간의 중심이 되는 조명", "집 안 분위기를 바꿔주는", "빛의 온기가 전해지는", "심플하면서도 감성적인", "빛으로 완성된 인테리어의",
            "아늑하고 부드러운 조명의", "감성적인 빛의 레이어를 더한", "고급스러움을 더하는 조명", "은은하게 빛나는 무드의", "하루의 피로를 풀어주는 조명의",
            "빛으로 휴식을 선사하는", "모던한 감성의 라이트", "따뜻함이 느껴지는 무드등의", "공간을 환하게 밝혀주는", "감각적인 빛으로 물든",
            "조화로운 조명 디자인의", "편안한 무드를 비춰주는", "빛의 방향으로 공간을 채우는", "자연광처럼 따뜻한", "감성적인 컬러의 조명으로",
            "포근한 분위기를 완성하는", "은은하게 공간을 비추는", "빛의 깊이를 느낄 수 있는", "세련된 라인의 조명으로", "모던한 무드의 라이트",
            "따뜻한 빛이 감도는", "감각적인 조명 포인트의", "은은한 분위기를 연출하는", "공간 속 포근한 빛의", "차분하게 공간을 밝혀주는",
            "집 안을 아늑하게 밝혀주는", "감성적인 컬러 라이트의", "고급스러운 조명 연출의", "빛으로 여유를 선사하는", "트렌디한 감성 조명의",
            "심플하지만 존재감 있는", "빛의 온도로 감성을 전하는", "포근한 감촉의 조명빛이 감도는", "모던하고 따뜻한 라이트의", "공간을 한층 밝혀주는",
            "감성적인 공간 조명의", "고요한 조도의 아름다움을 담은", "빛의 농도와 온도를 조화롭게 담은", "따뜻한 무드로 완성된", "집 안의 감성을 더하는",
            "빛으로 표현된 세련된 감성의", "부드럽게 퍼지는 조명의", "은은하게 머무는 빛의", "편안한 공간을 비추는", "감성적인 인테리어 무드의",
            "차분한 빛으로 공간을 물들이는", "은은한 무드를 완성하는", "조명 하나로 완성되는 감성의", "빛으로 따뜻함을 전하는", "세련된 감각의 라이트가 어우러진",
            "편안함과 여유를 더하는", "감성적인 빛으로 하루를 채우는", "모던한 분위기의 조명으로", "아늑함을 밝혀주는", "공간의 중심을 밝혀주는"
    );

    // 부가기능
    private static final List<String> FEATURES = Arrays.asList(
            "고깔형", "원통형", "구형", "돔형", "원반형",
            "프레임형", "전구형", "바형", "곡선형", "버블형"
    );

    // 재질
    private static final List<String> MATERIALS = Arrays.asList(
            "플라스틱", "유리", "도자기", "우드", "스톤",
            "메탈", "패브릭", "크리스탈", "종이", "라탄"
    );

    // 커버
    private static final List<String> COVER = Arrays.asList(
            "방", "거실", "다용도", "매립", "레일",
            "줄", "북라이트"
    );

    private static final List<String> TYPES = Arrays.asList(
            "플리커프리", "눈부심방지", "밝기조절", "리모컨", "컬러변화",
            "IOT기능", "각도조철", "터치스위치"
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
        return categoryName != null && categoryName.contains("천장등");
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
                    for (String type : TYPES) {
                        String descriptor = getRandomItem(DESCRIPTORS);

                        String productName = String.format("[%s] %s %s 재질의 %s %s %s (%s)",
                                brand, descriptor, material, feature, cover, categoryName, type);

                        allCombinations.add(productName);
                    }
                }
            }
        }

        log.info("천장등 {}개 조합 생성",
                allCombinations.size());

        return allCombinations;
    }

    private String getRandomItem(List<String> items) {
        return items.get(RANDOM.nextInt(items.size()));
    }
}
