package com.dev.core.ecommerce.service.review.response;

import com.dev.core.ecommerce.repository.review.response.ReviewRateSummary;

import java.math.BigDecimal;

public record RateSummary(
        Long count,
        BigDecimal rate
) {
    public static RateSummary of(ReviewRateSummary reviewRateSummary) {
        return new RateSummary(reviewRateSummary.count(), reviewRateSummary.rate());
    }
}
