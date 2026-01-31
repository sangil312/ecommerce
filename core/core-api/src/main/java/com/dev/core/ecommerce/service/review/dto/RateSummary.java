package com.dev.core.ecommerce.service.review.dto;

import com.dev.core.ecommerce.repository.review.dto.RateSummaryView;
import com.dev.core.ecommerce.repository.review.dto.RateSummaryGroupView;

import java.math.BigDecimal;

public record RateSummary(
        Long count,
        BigDecimal rate
) {
    public static RateSummary of(RateSummaryGroupView rateSummaryGroupView) {
        BigDecimal rate = rateSummaryGroupView.rate() == null
                ? BigDecimal.ZERO
                : rateSummaryGroupView.rate();

        return new RateSummary(rateSummaryGroupView.count(), rate);
    }

    public static RateSummary of(RateSummaryView rateSummary) {
        BigDecimal rate = rateSummary.rate() == null ? BigDecimal.ZERO : rateSummary.rate();
        return new RateSummary(rateSummary.count(), rate);
    }
}
