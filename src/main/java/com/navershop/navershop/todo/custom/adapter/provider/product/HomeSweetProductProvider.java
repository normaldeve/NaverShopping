package com.navershop.navershop.todo.custom.adapter.provider.product;

import com.navershop.navershop.template.adapter.provider.product.ProductProvider;
import com.navershop.navershop.todo.repository.product.Product;
import com.navershop.navershop.todo.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * HomeSweet 프로젝트의 ProductStorage 구현
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HomeSweetProductProvider implements ProductProvider<Product> {

    private final ProductRepository productRepository;

    @Override
    public boolean isDuplicate(Product product) {
        return productRepository.existsBySellerAndName(product.getSeller(), product.getName());
    }

    @Override
    public Product save(Product product) {
        try {
            Product saved = productRepository.save(product);
            log.debug("저장 완료: {}", product.getName());
            return saved;
        } catch (Exception e) {
            log.error("저장 실패: {}", product.getName(), e);
            throw e;
        }
    }
}
