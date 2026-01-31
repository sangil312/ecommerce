package com.dev.core.ecommerce.service.review.dto;

import java.math.BigDecimal;

public record ReviewContent(
        BigDecimal rate,
        String content
) {
    public static ReviewContent of(BigDecimal rate, String content) {
        return new ReviewContent(rate, content);
    }
}
