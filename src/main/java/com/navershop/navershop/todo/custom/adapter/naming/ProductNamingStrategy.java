package com.navershop.navershop.todo.custom.adapter.naming;

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

}
