package com.navershop.navershop.todo.repository.category;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 카테고리
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Entity
@Table(name = "product_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private Long parentId;

    @Column(nullable = false)
    @Min(0)
    @Max(2)
    private Integer depth;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public ProductCategory(Long id, String name, Long parentId, Integer depth) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.depth = depth;
    }
}
