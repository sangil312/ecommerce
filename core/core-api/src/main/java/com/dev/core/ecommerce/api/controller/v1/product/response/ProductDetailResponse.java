package com.dev.core.ecommerce.api.controller.v1.product.response;

import com.dev.core.ecommerce.domain.product.Product;

import java.math.BigDecimal;

public record ProductDetailResponse(
        Long productId,
        String name,
        String description,
        BigDecimal price
) {
    public static ProductDetailResponse of(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
