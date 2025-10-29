package com.homesweet.homesweetcrawler.template.adapter.provider.category;

import java.util.List;

/**
 * 카테고리 제공 인터페이스
 *
 * 👉 각 팀이 구현해야 하는 인터페이스
 *
 * @param <CATEGORY> 프로젝트의 Category 엔티티 타입
 */
public interface CategoryProvider<CATEGORY> {

    /**
     * 모든 카테고리 조회
     */
    List<CATEGORY> findAllCategories();

    /**
     * 카테고리 ID 추출
     */
    Long getCategoryId(CATEGORY category);

    /**
     * 카테고리 이름 추출
     */
    String getCategoryName(CATEGORY category);

    /**
     * 카테고리 Depth 추출
     */
    Integer getCategoryDepth(CATEGORY category);

    /**
     * 부모 카테고리 ID 추출
     */
    Long getParentCategoryId(CATEGORY category);
}
