package com.dev.core.ecommerce.repository.review.response;

import java.math.BigDecimal;
import java.util.Objects;

public record ReviewRateSummary(
        Long targetId,
        Long count,
        BigDecimal rate
) {
    public ReviewRateSummary(Long targetId, Long count, Double rate) {
        this(targetId, count, Objects.isNull(rate) ? BigDecimal.ZERO : BigDecimal.valueOf(rate));
    }
}
