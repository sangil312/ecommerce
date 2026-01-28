package com.dev.core.ecommerce.api.controller.v1.product.response;

import com.dev.core.ecommerce.service.review.response.RateSummary;
import com.dev.core.ecommerce.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
            Map<Long, RateSummary> reviewsRateSummary
    ) {
        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getThumbnailUrl(),
                        product.getShortDescription(),
                        product.getPrice(),
                        reviewsRateSummary.get(product.getId()).rate(),
                        reviewsRateSummary.get(product.getId()).count())
                )
                .toList();
    }
}
