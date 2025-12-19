package com.dev.ecommerce.controller.product.response;

import com.dev.ecommerce.domain.product.Product;

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
