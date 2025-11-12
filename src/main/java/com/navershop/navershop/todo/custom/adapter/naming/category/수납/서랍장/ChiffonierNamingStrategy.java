package com.navershop.navershop.todo.custom.adapter.naming.category.수납.서랍장;

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
public class ChiffonierNamingStrategy implements ProductNamingStrategy {

    private static final Random RANDOM = new Random();

    // 꾸미는말 - 랜덤
    private static final List<String> DESCRIPTORS = Arrays.asList(
            "깔끔한 수납이 돋보이는", "공간을 여유롭게 활용하는", "실용적인 디자인의", "모던한 감성의", "정돈된 공간을 완성하는",
            "심플하면서도 고급스러운", "여유로운 수납력을 자랑하는", "감각적인 인테리어에 어울리는", "효율적인 공간 구성을 위한", "편리한 정리를 도와주는",
            "따뜻한 우드톤의", "감성적인 수납 디자인의", "공간의 품격을 높이는", "모던하고 깔끔한", "어떤 공간에도 잘 어울리는",
            "다양한 수납을 가능하게 하는", "생활 공간에 실용성을 더하는", "깔끔하게 정리된 인테리어의", "세련된 라인의", "군더더기 없는 디자인의",
            "조화로운 수납감을 선사하는", "여유로운 공간감을 주는", "감각적인 컬러감의", "정돈된 분위기를 연출하는", "모던한 공간에 어울리는",
            "필요한 물건을 깔끔히 정리할 수 있는", "실용성과 디자인을 모두 갖춘", "공간 활용도를 높여주는", "아늑한 무드를 완성하는", "감성적인 수납 인테리어의",
            "자연스러운 질감의", "포근한 공간을 연출하는", "트렌디한 감각의", "고급스러운 질감의", "여유로운 정리 공간을 제공하는",
            "깔끔하고 실용적인", "감각적으로 디자인된", "공간을 효율적으로 채워주는", "따뜻한 온기를 더하는", "정리의 미학을 담은",
            "생활감이 묻어나는", "감성적인 공간을 완성하는", "실용미와 감성을 모두 담은", "모던한 인테리어의", "자연스러운 컬러감의",
            "공간의 균형을 이루는", "필요한 수납을 완벽히 충족하는", "깔끔함이 느껴지는", "세련되게 완성된", "감성적인 무드를 담은",
            "여유로운 수납 구조의", "실용적인 공간 구성을 위한", "감각적인 수납 솔루션의", "깔끔한 라인의", "부드러운 컬러감의",
            "정리정돈이 쉬운", "공간을 넓어 보이게 하는", "세련된 무드의", "모던하고 실용적인", "편안한 공간감을 선사하는",
            "깔끔하게 정리되는", "감성적인 수납가구의", "조화로운 인테리어를 위한", "고요한 감성을 담은", "트렌디한 실내 분위기의",
            "공간에 안정감을 주는", "생활 속 편리함을 더하는", "정리의 즐거움을 선사하는", "감성적인 우드톤의", "차분한 무드를 완성하는",
            "모던함과 따뜻함이 공존하는", "심플하고 정갈한", "공간을 품격 있게 완성하는", "편리함이 느껴지는", "감각적인 실용미의",
            "다양한 물건을 깔끔히 담아내는", "여유로운 수납 구성이 가능한", "고급스럽게 정돈된", "깔끔한 디자인의", "감성적인 공간 연출의",
            "실용적인 구조로 완성된", "정돈된 라이프스타일을 위한", "공간을 더 넓게 느끼게 하는", "감각적인 디테일이 돋보이는", "편안함을 담은 수납의",
            "트렌디한 실내 분위기를 완성하는", "생활 속 여유를 선사하는", "군더더기 없는 디자인으로 완성된", "자연스러운 공간을 만드는", "고급스러운 마감의",
            "감성적인 정리 공간의", "여유로움을 담은 수납의", "실용성과 감각을 동시에 담은", "공간을 더욱 빛나게 하는", "심플하면서도 세련된",
            "모던한 컬러톤의", "생활의 질을 높이는", "감성적인 분위기의 수납", "공간을 깔끔히 정리하는", "편리하고 실용적인",
            "감각적인 인테리어 수납의", "자연스러움을 담은 디자인의", "트렌디한 수납감의", "고급스러운 질감을 살린", "정리의 완성도를 높이는"
    );

    // 부가기능
    private static final List<String> FEATURES = Arrays.asList(
            "3단", "4단", "5단", "6단"
    );

    // 재질
    private static final List<String> MATERIALS = Arrays.asList(
            "원목", "유리", "라탄", "가죽", "메쉬",
            "플라스틱", "아크릴", "패브릭", "메탈", "스톤"
    );

    // 커버
    private static final List<String> COVER = Arrays.asList(
            "트롤리", "공간박스", "틈새", "서랍", "빨래",
            "옷", "이동식", "모듈형"
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
        return categoryName != null && categoryName.contains("서랍장");
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

        log.info("서랍장 {}개 조합 생성",
                allCombinations.size());

        return allCombinations;
    }

    private String getRandomItem(List<String> items) {
        return items.get(RANDOM.nextInt(items.size()));
    }
}
