package com.dev.core.ecommerce.service.review.dto;

import com.dev.core.enums.review.ReviewTargetType;

public record ReviewTarget(
        ReviewTargetType targetType,
        Long id
) {
    public static ReviewTarget of(ReviewTargetType targetType, Long id) {
        return new ReviewTarget(targetType, id);
    }
}
