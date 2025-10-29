package com.navershop.navershop.todo.repository.option;

import com.navershop.navershop.todo.repository.sku.ProductSkuOption;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 옵션 값
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Entity
@Table(name = "product_option_value")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProductOptionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_value_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String value;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id", nullable = false)
    private ProductOptionGroup group;

    @OneToMany(mappedBy = "optionValue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSkuOption> skuLinks = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public ProductOptionValue(Long id, String value) {
        this.id = id;
        this.value = value;
    }
}
