package com.dev.core.ecommerce.api.controller.v1.product.response;

import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.service.review.response.RateSummary;

import java.math.BigDecimal;
import java.util.Objects;

public record ProductDetailResponse(
        Long productId,
        String name,
        String thumbnailUrl,
        String description,
        String shortDescription,
        BigDecimal price,
        BigDecimal rate,
        Long rateCount
) {
    public static ProductDetailResponse of(Product product, RateSummary rateSummary) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getThumbnailUrl(),
                product.getDescription(),
                product.getShortDescription(),
                product.getPrice(),
                Objects.requireNonNull(rateSummary).rate() == null ? BigDecimal.ZERO : rateSummary.rate(),
                rateSummary.count()
        );
    }
}
