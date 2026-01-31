package com.dev.core.ecommerce.api.controller.v1.review.response;

import com.dev.core.ecommerce.domain.review.ReviewImage;

import java.util.List;

public record ReviewImageResponse(
        Long id,
        String imageUrl
) {
    public static List<ReviewImageResponse> of(List<ReviewImage> images) {
        return images.stream()
                .map(it -> new ReviewImageResponse(it.getId(), it.getImageUrl()))
                .toList();
    }
}
