package com.dev.core.ecommerce.service.review.response;

import java.math.BigDecimal;

public record RateSummary(
        Long count,
        BigDecimal rate
) {
}
