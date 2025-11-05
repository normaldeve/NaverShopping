package com.navershop.navershop.template.adapter.provider.product;

/**
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */

import java.util.List;

/**
 * Product 저장 인터페이스
 *
 *  각 팀이 구현해야 하는 인터페이스
 *
 * @param <PRODUCT> 프로젝트의 Product 엔티티 타입
 */
public interface ProductProvider<PRODUCT> {

    /**
     * Product 저장
     *
     * @param product Product 엔티티
     * @return 저장된 Product
     */
    PRODUCT save(PRODUCT product);
}
