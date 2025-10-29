package com.navershop.navershop.todo.repository.sku;

import com.navershop.navershop.todo.repository.option.ProductOptionValue;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Entity
@Table(name = "product_sku_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProductSkuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_option_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_value_id", nullable = false)
    private ProductOptionValue optionValue;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public ProductSkuOption(Long id, Sku sku, ProductOptionValue optionValue) {
        this.sku = sku;
        this.optionValue = optionValue;
    }
}
