package com.dev.ecommerce.domain.product;

import com.dev.ecommerce.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "product_category")
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
