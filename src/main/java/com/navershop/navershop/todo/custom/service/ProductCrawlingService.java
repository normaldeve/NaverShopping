package com.navershop.navershop.todo.custom.service;


import com.navershop.navershop.template.adapter.provider.category.CategoryProvider;
import com.navershop.navershop.template.adapter.mapper.ProductMapper;
import com.navershop.navershop.template.adapter.option.OptionGenerator;
import com.navershop.navershop.template.adapter.provider.product.ProductProvider;
import com.navershop.navershop.template.adapter.provider.user.UserProvider;
import com.navershop.navershop.core.api.NaverShoppingApiClient;
import com.navershop.navershop.template.service.BaseCrawlingService;
import com.navershop.navershop.todo.custom.adapter.naming.ProductNameFactory;
import com.navershop.navershop.todo.repository.product.Product;
import com.navershop.navershop.todo.repository.category.ProductCategory;
import com.navershop.navershop.todo.repository.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 실제 사용할 크롤링 서비스
 */
@Service
public class ProductCrawlingService extends BaseCrawlingService<Product, ProductCategory, User> {

    public ProductCrawlingService(
            NaverShoppingApiClient apiClient,
            ProductMapper<Product, ProductCategory, User> productMapper,
            ProductProvider<Product> productProvider,
            CategoryProvider<ProductCategory> categoryProvider,
            UserProvider<User> userProvider,
            @Autowired(required = false) OptionGenerator<Product> optionGenerator,
            ProductNameFactory productNameFactory) {
        super(apiClient, productMapper, productProvider, categoryProvider, userProvider, optionGenerator, productNameFactory);
    }
}