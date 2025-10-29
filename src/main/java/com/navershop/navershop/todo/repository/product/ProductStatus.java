package com.navershop.navershop.todo.repository.product;

import lombok.Getter;

/**
 * 제품 상태
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Getter
public enum ProductStatus {
    ON_SALE,
    OUT_OF_STOCK,
    SUSPENDED;
}
