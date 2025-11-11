package com.navershop.navershop.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.navershop.navershop.template.adapter.provider.product.ProductProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 상품 배치 저장 전용 서비스
 *
 */
@Slf4j
@Service
public abstract class ProductBatchSaveService<PRODUCT> {

    protected final ProductProvider<PRODUCT> productProvider;

    protected ProductBatchSaveService(ProductProvider<PRODUCT> productProvider) {
        this.productProvider = productProvider;
    }

    /**
     * 버퍼 저장 및 메모리 해제 (트랜잭션 적용)
     */
    @Transactional
    public int saveAndClearBuffer(List<PRODUCT> productBuffer) {
        try {
            int saved = productProvider.saveAll(new ArrayList<>(productBuffer));
            log.debug("   💾 버퍼 저장: {}개", saved);
            productBuffer.clear(); // 메모리 즉시 해제
            return saved;
        } catch (Exception e) {
            log.error("   ❌ 버퍼 저장 실패", e);
            productBuffer.clear();
            return 0;
        }
    }
}
