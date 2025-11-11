package com.navershop.navershop.todo.custom.service;

import com.navershop.navershop.template.adapter.provider.product.ProductProvider;
import com.navershop.navershop.template.service.ProductBatchSaveService;
import com.navershop.navershop.todo.repository.product.Product;
import org.springframework.stereotype.Service;

/**
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 11.
 */
@Service
public class ProductBatchSaveServiceImpl extends ProductBatchSaveService<Product> {

    public ProductBatchSaveServiceImpl(ProductProvider<Product> productProvider) {
        super(productProvider);
    }
}
