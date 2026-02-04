package com.dev.core.ecommerce.api.controller.v1.review.response;

import com.dev.core.ecommerce.service.review.dto.ReviewAndImage;
import com.dev.core.enums.review.ReviewTargetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long id,
        ReviewTargetType targetType,
        Long targetId,
        String targetName,
        BigDecimal rate,
        String content,
        LocalDateTime createdAt,
        List<ReviewImageResponse> images
) {
    public static List<ReviewResponse> of(
            List<ReviewAndImage> reviews,
            String productName
    ) {
        return reviews.stream()
                .map(it ->
                        new ReviewResponse(
                            it.reviewId(),
                            it.target().targetType(),
                            it.target().id(),
                            productName,
                            it.content().rate(),
                            it.content().content(),
                            it.createdAt(),
                            ReviewImageResponse.of(it.images())
                        )
                )
                .toList();
    }
}
