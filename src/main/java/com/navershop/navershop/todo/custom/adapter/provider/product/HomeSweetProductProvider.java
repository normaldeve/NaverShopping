package com.navershop.navershop.todo.custom.adapter.provider.product;

import com.navershop.navershop.template.adapter.provider.product.ProductProvider;
import com.navershop.navershop.todo.repository.product.Product;
import com.navershop.navershop.todo.repository.product.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * HomeSweet 프로젝트의 ProductStorage 구현
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HomeSweetProductProvider implements ProductProvider<Product> {

    private final ProductRepository productRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    private static final int BATCH_SIZE = 200;

    @Override
    public int saveAll(List<Product> products) {
        if (products.isEmpty()) return 0;

        log.info("💾 제품 배치 저장 시작 (총 {}개)", products.size());
        int savedCount = 0;

        for (int i = 0; i < products.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, products.size());
            List<Product> batch = products.subList(i, end);

            try {
                productRepository.saveAll(batch);
                entityManager.flush();
                entityManager.clear();

                savedCount += batch.size();
                log.info("✅ 배치 저장 완료: {}-{}", i, end);
            } catch (Exception e) {
                log.error("❌ 배치 저장 실패: {}-{}", i, end, e);
            }
        }

        log.info("💾 전체 제품 저장 완료 ({}건)", savedCount);
        return savedCount;
    }
}
