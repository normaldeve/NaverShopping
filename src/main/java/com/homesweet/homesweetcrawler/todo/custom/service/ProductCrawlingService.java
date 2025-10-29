package com.homesweet.homesweetcrawler.todo.custom.service;


import com.homesweet.homesweetcrawler.template.adapter.provider.category.CategoryProvider;
import com.homesweet.homesweetcrawler.template.adapter.mapper.ProductMapper;
import com.homesweet.homesweetcrawler.template.adapter.option.OptionGenerator;
import com.homesweet.homesweetcrawler.template.adapter.provider.product.ProductProvider;
import com.homesweet.homesweetcrawler.template.adapter.provider.user.UserProvider;
import com.homesweet.homesweetcrawler.core.api.NaverShoppingApiClient;
import com.homesweet.homesweetcrawler.template.service.BaseCrawlingService;
import com.homesweet.homesweetcrawler.todo.repository.product.Product;
import com.homesweet.homesweetcrawler.todo.repository.category.ProductCategory;
import com.homesweet.homesweetcrawler.todo.repository.user.User;
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
            @Autowired(required = false) OptionGenerator<Product> optionGenerator) {
        super(apiClient, productMapper, productProvider, categoryProvider, userProvider, optionGenerator);
    }
}