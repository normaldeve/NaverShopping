package com.navershop.navershop.todo.repository.sku;

import com.navershop.navershop.todo.repository.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Sku
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Entity
@Table(name = "sku")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "price_adjustment", nullable = false)
    private Integer priceAdjustment = 0;

    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity = 0L;

    @Builder.Default
    @OneToMany(mappedBy = "sku", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSkuOption> skuOptions = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Sku(Integer priceAdjustment, Long stockQuantity) {
        this.priceAdjustment = priceAdjustment;
        this.stockQuantity = stockQuantity;
    }

    public void addSkuOption(ProductSkuOption skuOption) {
        this.skuOptions.add(skuOption);
        skuOption.setSku(this);
    }
}
