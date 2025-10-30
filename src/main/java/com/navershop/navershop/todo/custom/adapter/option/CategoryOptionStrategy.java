package com.navershop.navershop.todo.custom.adapter.option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CategoryOptionStrategy {

    /**
     * 카테고리별 옵션 설정 정의
     */
    private static final Map<String, CategoryOptionConfig> CATEGORY_CONFIGS = new HashMap<>();

    static {
        // 침대 카테고리
        CATEGORY_CONFIGS.put("침대", CategoryOptionConfig.builder()
                .optionGroups(List.of(
                        OptionGroupConfig.of("사이즈", List.of(
                                OptionValueConfig.of("싱글", 0, 100L),
                                OptionValueConfig.of("퀸", 100000, 60L),
                                OptionValueConfig.of("킹", 150000, 40L)
                        )),
                        OptionGroupConfig.of("색상", List.of(
                                OptionValueConfig.of("화이트", 0, 50L),
                                OptionValueConfig.of("블랙", 10000, 50L)
                        ))
                ))
                .build());

        // 소파 카테고리
        CATEGORY_CONFIGS.put("소파", CategoryOptionConfig.builder()
                .optionGroups(List.of(
                        OptionGroupConfig.of("인원", List.of(
                                OptionValueConfig.of("1인용", 0, 80L),
                                OptionValueConfig.of("2인용", 100000, 60L),
                                OptionValueConfig.of("3인용", 200000, 40L)
                        )),
                        OptionGroupConfig.of("재질", List.of(
                                OptionValueConfig.of("패브릭", 0, 50L),
                                OptionValueConfig.of("천연가죽", 300000, 30L),
                                OptionValueConfig.of("인조가죽", 150000, 40L)
                        ))
                ))
                .build());

        // 침구 카테고리 (유아용)
        CATEGORY_CONFIGS.put("침구", CategoryOptionConfig.builder()
                .optionGroups(List.of(
                        OptionGroupConfig.of("사이즈", List.of(
                                OptionValueConfig.of("유아용(100x120)", 0, 100L),
                                OptionValueConfig.of("주니어용(120x150)", 20000, 80L)
                        )),
                        OptionGroupConfig.of("색상", List.of(
                                OptionValueConfig.of("핑크", 0, 60L),
                                OptionValueConfig.of("블루", 0, 60L),
                                OptionValueConfig.of("옐로우", 0, 50L),
                                OptionValueConfig.of("그린", 0, 50L)
                        ))
                ))
                .build());

        // 장난감 카테고리
        CATEGORY_CONFIGS.put("장난감", CategoryOptionConfig.builder()
                .optionGroups(List.of(
                        OptionGroupConfig.of("종류", List.of(
                                OptionValueConfig.of("블록", 0, 100L),
                                OptionValueConfig.of("인형", 5000, 80L),
                                OptionValueConfig.of("퍼즐", 3000, 90L)
                        )),
                        OptionGroupConfig.of("색상", List.of(
                                OptionValueConfig.of("레드", 0, 50L),
                                OptionValueConfig.of("블루", 0, 50L),
                                OptionValueConfig.of("그린", 0, 40L)
                        ))
                ))
                .build());

        // 책상 카테고리
        CATEGORY_CONFIGS.put("책상", CategoryOptionConfig.builder()
                .optionGroups(List.of(
                        OptionGroupConfig.of("사이즈", List.of(
                                OptionValueConfig.of("1000mm", 0, 80L),
                                OptionValueConfig.of("1200mm", 50000, 60L),
                                OptionValueConfig.of("1400mm", 80000, 40L)
                        )),
                        OptionGroupConfig.of("색상", List.of(
                                OptionValueConfig.of("오크", 0, 50L),
                                OptionValueConfig.of("월넛", 30000, 40L),
                                OptionValueConfig.of("화이트", 0, 60L)
                        ))
                ))
                .build());

        // 주방용품 카테고리 (단일 옵션)
        CATEGORY_CONFIGS.put("주방용품", CategoryOptionConfig.builder()
                .optionGroups(List.of(
                        OptionGroupConfig.of("색상", List.of(
                                OptionValueConfig.of("화이트", 0, 100L),
                                OptionValueConfig.of("블랙", 0, 80L),
                                OptionValueConfig.of("실버", 0, 70L)
                        ))
                ))
                .build());
    }

    /**
     * 카테고리에 맞는 옵션 설정 조회
     */
    public CategoryOptionConfig getConfig(String categoryName) {
        // 정확히 일치하는 카테고리가 있으면 반환
        if (CATEGORY_CONFIGS.containsKey(categoryName)) {
            return CATEGORY_CONFIGS.get(categoryName);
        }

        // 부분 일치로 찾기 (예: "유아 침구" -> "침구")
        for (Map.Entry<String, CategoryOptionConfig> entry : CATEGORY_CONFIGS.entrySet()) {
            if (categoryName.contains(entry.getKey())) {
                log.info("카테고리 '{}' -> '{}' 옵션 설정 사용", categoryName, entry.getKey());
                return entry.getValue();
            }
        }

        // 기본 옵션 설정 반환
        log.warn("카테고리 '{}'에 대한 옵션 설정이 없습니다. 기본 설정 사용", categoryName);
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









