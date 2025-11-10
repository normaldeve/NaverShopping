package com.navershop.navershop.todo.custom.adapter.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * 제품 카테고리 Enum
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 7.
 */
@Getter
@RequiredArgsConstructor
public enum ProductCategory {

    FURNITURE("가구"),
    FABRIC("패브릭"),
    ELECTRONICS("가전·디지털"),
    KITCHEN("주방용품"),
    DECOR_PLANT("데코·식물"),
    STORAGE("수납·정리"),
    KIDS("유아·아동"),
    LIVING("생활용품"),
    PET("반려동물"),
    CAMPING("캠핑·레저"),
    RENTAL("렌탈"),
    SHOPPING("장보기"),
    TOOL("공구"),
    LIGHTING("조명");

    private final String displayName;

    public static Optional<ProductCategory> fromDisplayName(String name) {
        return Arrays.stream(values())
                .filter(c -> c.displayName.equals(name))
                .findFirst();
    }
}
