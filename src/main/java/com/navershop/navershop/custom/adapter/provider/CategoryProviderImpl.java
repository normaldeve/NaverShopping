package com.navershop.navershop.custom.adapter.provider;

import com.navershop.navershop.custom.entity.Category;
import com.navershop.navershop.custom.entity.repository.CategoryRepository;
import com.navershop.navershop.template.adapter.provider.category.CategoryProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 카테고리 관련 구현해야 하는 코드
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Component
@RequiredArgsConstructor
public class CategoryProviderImpl implements CategoryProvider<Category> {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findAllCategories() {
        return List.of();
    }

    @Override
    public Long getCategoryId(Category category) {
        return 0L;
    }

    @Override
    public String getCategoryName(Category category) {
        return "";
    }

    @Override
    public Long getParentCategoryId(Category category) {
        return 0L;
    }

    @Override
    public Category findById(Long categoryId) {
        return null;
    }
}
