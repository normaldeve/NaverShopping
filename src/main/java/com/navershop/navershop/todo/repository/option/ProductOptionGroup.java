package com.navershop.navershop.todo.repository.option;

import com.navershop.navershop.todo.repository.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 옵션 그룹
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Entity
@Table(name = "product_option_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProductOptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_group_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String groupName;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionValue> values = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public ProductOptionGroup(String groupName, List<ProductOptionValue> values) {
        this.groupName = groupName;
        if (values != null) {
            values.forEach(this::addOptionValue);
        }
    }

    public void addOptionValue(ProductOptionValue value) {
        values.add(value);
        value.setGroup(this);
    }
}
