package com.dev.core.ecommerce.api.controller.v1.review.usecase;

import com.dev.core.ecommerce.api.controller.v1.review.response.ReviewListResponse;
import com.dev.core.ecommerce.api.controller.v1.review.response.ReviewResponse2;
import com.dev.core.ecommerce.service.product.ProductService;
import com.dev.core.ecommerce.service.review.ReviewService;
import com.dev.core.enums.review.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReviewUseCase {
    private final ReviewService reviewService;
    private final ProductService productService;

    public ReviewListResponse findReviews(
            ReviewTargetType targetType,
            Long targetId,
            Pageable pageable
    ) {
        var product = productService.findProduct(targetId);

        var rateSummary = reviewService.findReviewRateSummary(targetType, targetId);

        var reviewsPage = reviewService.findReviews(targetType, targetId, pageable);

        var responses = ReviewResponse2.of(reviewsPage.contents(), product.getName());

        return ReviewListResponse.of(product.getName(), rateSummary, responses, reviewsPage.hasNext());
    }
}
