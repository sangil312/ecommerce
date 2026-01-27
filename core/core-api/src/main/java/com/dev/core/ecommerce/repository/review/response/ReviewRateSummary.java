package com.dev.core.ecommerce.repository.review.response;

import java.math.BigDecimal;

public record ReviewRateSummary(
        Long targetId,
        Long count,
        BigDecimal rate
) {
}
