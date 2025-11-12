package com.navershop.navershop.todo.custom.adapter.option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class CategoryOptionStrategy {

    private final List<String> AVAILABLE_COLORS = new ArrayList<>(List.of(
            "화이트", "블랙", "브라운", "골드", "오렌지", "그린",
            "네이비", "핑크", "그레이", "베이지", "실버",
            "레드", "옐로우", "블루"
    ));

    private final List<String> BED_OPTIONS_POOL = new ArrayList<>(List.of(
            "USB포트추가", "조명추가", "서랍추가", "헤드조명", "수납추가", "헤드추가"
    ));

    private final List<String> SOFA_OPTIONS_POOL = new ArrayList<>(List.of(
            "쿠션포함", "카우치포함", "헤드레스트 추가"
    ));

    private final List<String> LENGTH = new ArrayList<>(List.of(
            "100cm", "130cm", "150cm", "170cm", "190cm", "210cm", "230cm", "250cm", "270cm", "290cm"
    ));

    private static List<OptionValueConfig> randomOptionsFromPool(List<String> pool, int min, int max, boolean includeBasic) {
        List<OptionValueConfig> result = new ArrayList<>();

        if (includeBasic) {
            result.add(OptionValueConfig.of("기본", 0, 100L));
        }

        List<String> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, ThreadLocalRandom.current());
        int count = ThreadLocalRandom.current().nextInt(min, max + 1);
        return shuffled.stream()
                .limit(count)
                .map(opt -> OptionValueConfig.of(opt, 10000 * new Random().nextInt(5), 10000L))
                .toList();
    }

    public CategoryOptionConfig createRandomConfig(String categoryName) {
        if (categoryName.contains("침대")) {
            return CategoryOptionConfig.builder()
                    .optionGroups(List.of(
                            OptionGroupConfig.of("옵션", randomOptionsFromPool(BED_OPTIONS_POOL, 2, 3, true)),
                            OptionGroupConfig.of("색상", randomOptionsFromPool(AVAILABLE_COLORS, 2, 4, false))
                    ))
                    .build();
        }

        if (categoryName.contains("소파")) {
            return CategoryOptionConfig.builder()
                    .optionGroups(List.of(
                            OptionGroupConfig.of("옵션", randomOptionsFromPool(SOFA_OPTIONS_POOL, 2, 3, true)),
                            OptionGroupConfig.of("색상", randomOptionsFromPool(AVAILABLE_COLORS, 2, 4, false))
                    ))
                    .build();
        }

        if (categoryName.contains("의자")) {
            return CategoryOptionConfig.builder()
                    .optionGroups(List.of(
                            OptionGroupConfig.of("등판색상", randomOptionsFromPool(AVAILABLE_COLORS, 2, 4, false)),
                            OptionGroupConfig.of("좌판색상", randomOptionsFromPool(AVAILABLE_COLORS, 2, 4, false))
                    ))
                    .build();
        }

        if (categoryName.contains("러그")) {
            return CategoryOptionConfig.builder()
                    .optionGroups(List.of(
                            OptionGroupConfig.of("가로", randomOptionsFromPool(LENGTH, 3, 4, false)),
                            OptionGroupConfig.of("세로", randomOptionsFromPool(LENGTH, 3, 4, false))
                    ))
                    .build();
        }

        if (categoryName.contains("토퍼")) {
            return CategoryOptionConfig.builder()
                    .optionGroups(List.of(
                            OptionGroupConfig.of("가로", randomOptionsFromPool(LENGTH, 3, 4, false)),
                            OptionGroupConfig.of("세로", randomOptionsFromPool(LENGTH, 3, 4, false))
                    ))
                    .build();
        }

        if (categoryName.contains("커튼")) {
            return CategoryOptionConfig.builder()
                    .optionGroups(List.of(
                            OptionGroupConfig.of("가로", randomOptionsFromPool(LENGTH, 3, 4, false)),
                            OptionGroupConfig.of("세로", randomOptionsFromPool(LENGTH, 3, 4, false))
                    ))
                    .build();
        }

        if (categoryName.contains("선반")) {
            return CategoryOptionConfig.builder()
                    .optionGroups(List.of(
                            OptionGroupConfig.of("가로", randomOptionsFromPool(LENGTH, 3, 4, false)),
                            OptionGroupConfig.of("세로", randomOptionsFromPool(LENGTH, 3, 4, false)),
                            OptionGroupConfig.of("높이", randomOptionsFromPool(LENGTH, 3, 4, false))
                    ))
                    .build();
        }

        if (categoryName.contains("서랍장")) {
            return CategoryOptionConfig.builder()
                    .optionGroups(List.of(
                            OptionGroupConfig.of("가로", randomOptionsFromPool(LENGTH, 3, 4, false)),
                            OptionGroupConfig.of("세로", randomOptionsFromPool(LENGTH, 3, 4, false)),
                            OptionGroupConfig.of("높이", randomOptionsFromPool(LENGTH, 3, 4, false))
                    ))
                    .build();
        }

        // 기본
        return getDefaultConfig();
    }

    /**
     * 기본 옵션 설정
     */
    private CategoryOptionConfig getDefaultConfig() {
        return CategoryOptionConfig.builder()
                .optionGroups(List.of(
                        OptionGroupConfig.of("색상", List.of(
                                OptionValueConfig.of("화이트", 0, 100L),
                                OptionValueConfig.of("블랙", 0, 80L)
                        ))
                ))
                .build();
    }

    /**
     * 해당 카테고리가 옵션이 필요한지 확인
     */
    public boolean needsOptions(String categoryName) {
        // 모든 카테고리에 옵션 적용
        return true;
    }

    // ============ 내부 설정 클래스들 ============

    @Builder
    @Getter
    public static class CategoryOptionConfig {
        private List<OptionGroupConfig> optionGroups;
    }

    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class OptionGroupConfig {
        private String groupName;
        private List<OptionValueConfig> values;
    }

    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class OptionValueConfig {
        private String value;          // 옵션 값 (예: "싱글", "퀸")
        private Integer priceAdjustment; // 추가 금액
        private Long stock;              // 재고
    }
}