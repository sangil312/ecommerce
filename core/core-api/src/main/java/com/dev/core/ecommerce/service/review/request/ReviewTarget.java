package com.dev.core.ecommerce.service.review.request;

import com.dev.core.enums.review.ReviewTargetType;

public record ReviewTarget(
        ReviewTargetType targetType,
        Long id
) {
}
