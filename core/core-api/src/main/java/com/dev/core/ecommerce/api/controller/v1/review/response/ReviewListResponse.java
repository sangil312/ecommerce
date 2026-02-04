package com.dev.core.ecommerce.api.controller.v1.review.response;

import com.dev.core.ecommerce.service.review.dto.RateSummary;

import java.util.List;

public record ReviewListResponse(
        String productName,
        RateSummary rateSummary,
        List<ReviewResponse> contents,
        Boolean hasNext
) {
    public static ReviewListResponse of(
            String productName,
            RateSummary rateSummary,
            List<ReviewResponse> reviewResponsePage,
            Boolean hasNext
    ) {

        return new ReviewListResponse(productName, rateSummary, reviewResponsePage, hasNext);
    }
}
