package com.homesweet.homesweetcrawler.todo.custom.adapter.provider.category;

import com.homesweet.homesweetcrawler.todo.repository.category.ProductCategory;
import com.homesweet.homesweetcrawler.template.adapter.provider.category.CategoryProvider;
import com.homesweet.homesweetcrawler.todo.repository.category.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * HomeSweet 프로젝트의 CategoryProvider 구현
 */
@Component
@RequiredArgsConstructor
public class HomeSweetCategoryProvider implements CategoryProvider<ProductCategory> {

    private final ProductCategoryRepository categoryRepository;

    @Override
    public List<ProductCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Long getCategoryId(ProductCategory category) {
        return category.getId();
    }

    @Override
    public String getCategoryName(ProductCategory category) {
        return category.getName();
    }

    @Override
    public Integer getCategoryDepth(ProductCategory category) {
        return category.getDepth();
    }

    @Override
    public Long getParentCategoryId(ProductCategory category) {
        return category.getParentId();
    }
}
