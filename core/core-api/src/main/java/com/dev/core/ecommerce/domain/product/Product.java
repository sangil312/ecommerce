package com.dev.core.ecommerce.domain.product;

import com.dev.core.ecommerce.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product")
public class Product extends BaseEntity {
    private String name;
    private String thumbnailUrl;
    private String description;
    private String shortDescription;
    private BigDecimal price;

    public static Product create(
            String name,
            String thumbnailUrl,
            String description,
            String shortDescription,
            BigDecimal price
    ) {
        Product product = new Product();
        product.name = name;
        product.thumbnailUrl = thumbnailUrl;
        product.description = description;
        product.shortDescription = shortDescription;
        product.price = price;
        return product;
    }

}
