package com.dev.core.ecommerce.controller.v1.review.response;

import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.enums.review.ReviewTargetType;

import java.math.BigDecimal;
import java.util.List;

public record ReviewResponse(
        Long id,
        ReviewTargetType targetType,
        Long targetId,
        BigDecimal rate,
        String content
) {
    public static List<ReviewResponse> of(List<Review> reviews) {
        return reviews.stream()
                .map(it ->
                        new ReviewResponse(
                                it.getId(),
                                it.getTargetType(),
                                it.getTargetId(),
                                it.getRate(),
                                it.getContent()
                        )
                )
                .toList();
    }
}
