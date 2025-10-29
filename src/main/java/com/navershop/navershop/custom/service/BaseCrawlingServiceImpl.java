package com.navershop.navershop.custom.service;

import com.navershop.navershop.core.api.NaverShoppingApiClient;
import com.navershop.navershop.custom.entity.Category;
import com.navershop.navershop.custom.entity.Product;
import com.navershop.navershop.custom.entity.User;
import com.navershop.navershop.template.adapter.mapper.ProductMapper;
import com.navershop.navershop.template.adapter.option.OptionGenerator;
import com.navershop.navershop.template.adapter.provider.category.CategoryProvider;
import com.navershop.navershop.template.adapter.provider.product.ProductProvider;
import com.navershop.navershop.template.adapter.provider.user.UserProvider;
import com.navershop.navershop.template.service.BaseCrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Product, Category, User 엔티티를 넣어주세요!
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Service
public class BaseCrawlingServiceImpl extends BaseCrawlingService<Product, Category, User> {

    public BaseCrawlingServiceImpl(
            NaverShoppingApiClient apiClient,
            ProductMapper<Product, Category, User> productMapper,
            ProductProvider<Product> productProvider,
            CategoryProvider<Category> categoryProvider,
            UserProvider<User> userProvider,
            @Autowired(required = false) OptionGenerator<Product> optionGenerator) {
        super(apiClient, productMapper, productProvider, categoryProvider, userProvider, optionGenerator);
    }
}
