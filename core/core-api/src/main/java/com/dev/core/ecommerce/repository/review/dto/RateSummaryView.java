package com.dev.core.ecommerce.repository.review.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record RateSummaryView(
        Long count,
        BigDecimal rate
) {
    public RateSummaryView(Long count, Double rate) {
        this(count, Objects.isNull(rate) ? BigDecimal.ZERO : BigDecimal.valueOf(rate));
    }
}
