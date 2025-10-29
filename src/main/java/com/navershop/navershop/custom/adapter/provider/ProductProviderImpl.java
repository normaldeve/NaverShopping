package com.navershop.navershop.custom.adapter.provider;

import com.navershop.navershop.custom.entity.Product;
import com.navershop.navershop.custom.entity.repository.ProductRepository;
import com.navershop.navershop.template.adapter.provider.product.ProductProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 제품 관련 구현해야 하는 코드
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Component
@RequiredArgsConstructor
public class ProductProviderImpl implements ProductProvider<Product> {

    private final ProductRepository productRepository;

    @Override
    public boolean isDuplicate(Product product) {
        return false;
    }

    @Override
    public Product save(Product product) {
        return null;
    }
}
