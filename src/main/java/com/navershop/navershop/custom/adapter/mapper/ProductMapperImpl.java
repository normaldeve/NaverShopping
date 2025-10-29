package com.navershop.navershop.custom.adapter.mapper;

import com.navershop.navershop.core.dto.NaverShoppingResponse;
import com.navershop.navershop.custom.entity.Category;
import com.navershop.navershop.custom.entity.Product;
import com.navershop.navershop.custom.entity.User;
import com.navershop.navershop.template.adapter.mapper.ProductMapper;
import org.springframework.stereotype.Component;

/**
 * 제품 <-> 네이버 제품 매핑
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Component
public class ProductMapperImpl implements ProductMapper<Product, Category, User> {

    @Override
    public Product map(NaverShoppingResponse.NaverShoppingItem item, Category category, User seller) {
        return null;
    }
}
