package com.dev.core.ecommerce.domain.product;

import com.dev.core.ecommerce.support.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "product_category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_id_category_id", columnNames = {"product_id", "category_id"})
        }
)
public class ProductCategory extends BaseEntity {
    private Long productId;
    private Long categoryId;

    public static ProductCategory create(Long productId, Long categoryId) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.productId = productId;
        productCategory.categoryId = categoryId;
        return productCategory;
    }
}
