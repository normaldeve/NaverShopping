package com.navershop.navershop.todo.custom.adapter.naming;

import java.util.Collections;
import java.util.List;

/**
 * 카테고리별 상품명 생성 전략 인터페이스
 *
 * 상품명 형식: [브랜드] [꾸미는말] [옵션1] [옵션2] [카테고리]
 * 예: "한샘 호텔 철제 퀸사이즈 수납침대"
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 10.
 */
public interface ProductNamingStrategy {

    /**
     * 카테고리별 상품명 생성
     */
    String generateProductName(String brand, String categoryName);

    /**
     * 해당 전략이 적용 가능한 카테고리인지 확인
     */
    boolean supports(String categoryName);

    /**
     * 모든 조합 생성 지원 여부 (기본값: false)
     */
    default boolean supportsAllCombinations() {
        return false;
    }

    /**
     * 모든 조합의 상품명 생성 (기본 구현: 빈 리스트)
     */
    default List<String> generateAllCombinations(String brand, String categoryName) {
        return Collections.emptyList();
    }

}
