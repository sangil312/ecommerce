package com.dev.core.ecommerce.controller.v1.review;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.controller.v1.response.ApiResponse;
import com.dev.core.ecommerce.controller.v1.response.PageResponse;
import com.dev.core.ecommerce.controller.v1.review.request.CreateReviewRequest;
import com.dev.core.ecommerce.controller.v1.review.response.ReviewResponse;
import com.dev.core.ecommerce.service.review.ReviewService;
import com.dev.core.enums.review.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/v1/reviews")
    public ApiResponse<Object> create(
            User user,
            @RequestBody CreateReviewRequest request
    ) {
        reviewService.creat(user, request.toTarget(), request.toContent());
        return ApiResponse.success();
    }

    @GetMapping("/v1/reviews")
    public ApiResponse<PageResponse<ReviewResponse>> findReviews(
            @RequestParam ReviewTargetType targetType,
            @RequestParam Long targetId,
            Pageable pageable
    ) {
        var result = reviewService.findReviews(targetType, targetId, pageable);
        return ApiResponse.success(PageResponse.of(ReviewResponse.of(result.contents()), result.hasNext()));
    }
}
