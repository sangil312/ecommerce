package com.dev.core.ecommerce.repository.review.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record RateSummaryGroupView(
        Long targetId,
        Long count,
        BigDecimal rate
) {
    public RateSummaryGroupView(Long targetId, Long count, Double rate) {
        this(targetId, count, Objects.isNull(rate) ? BigDecimal.ZERO : BigDecimal.valueOf(rate));
    }
}
