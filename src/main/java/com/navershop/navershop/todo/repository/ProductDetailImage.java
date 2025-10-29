package com.navershop.navershop.todo.repository;

import com.navershop.navershop.todo.repository.product.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 제품 상세 페이지
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Entity
@Table(name = "products_detail_image")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ProductDetailImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public ProductDetailImage(Long id, Product product, String imageUrl) {
        this.id = id;
        this.product = product;
        this.imageUrl = imageUrl;
    }
}
