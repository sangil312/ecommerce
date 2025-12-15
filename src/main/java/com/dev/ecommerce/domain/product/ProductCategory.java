package com.dev.ecommerce.domain.product;

import com.dev.ecommerce.common.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;

@Getter
@Entity
public class ProductCategory extends BaseEntity {
    private Long productId;
    private Long categoryId;
}
