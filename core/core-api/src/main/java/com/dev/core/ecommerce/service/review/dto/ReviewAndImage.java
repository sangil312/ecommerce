package com.dev.core.ecommerce.service.review.dto;

import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.domain.review.ReviewImage;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewAndImage(
        Long reviewId,
        Long userId,
        String reviewKey,
        ReviewTarget target,
        ReviewContent content,
        LocalDateTime createdAt,
        List<ReviewImage> images
) {
    public static ReviewAndImage of(Review review, List<ReviewImage> images) {
        return new ReviewAndImage(
                review.getId(),
                review.getUserId(),
                review.getReviewKey(),
                ReviewTarget.of(review.getTargetType(), review.getTargetId()),
                ReviewContent.of(review.getRate(), review.getContent()),
                review.getCreatedAt(),
                images
        );
    }
}
