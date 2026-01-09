package com.dev.ecommerce.controller.v1.product.response;

import com.dev.ecommerce.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long productId,
        String name,
        String description,
        BigDecimal price
) {
    public static List<ProductResponse> of(List<Product> products) {
        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice())
                )
                .toList();
    }
}
