package com.dev.core.ecommerce.controller.v1.product.response;

import com.dev.core.ecommerce.service.review.response.RateSummary;
import com.dev.core.ecommerce.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long productId,
        String name,
        String thumbnailUrl,
        String shortDescription,
        BigDecimal price,
        BigDecimal rate,
        Long rateCount
        ) {
    public static List<ProductResponse> of(
            List<Product> products,
            RateSummary rateSummary
    ) {
        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getThumbnailUrl(),
                        product.getShortDescription(),
                        product.getPrice(),
                        rateSummary.rate(),
                        rateSummary.count())
                )
                .toList();
    }
}
