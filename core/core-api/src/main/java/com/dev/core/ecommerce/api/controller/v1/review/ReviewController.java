package com.dev.core.ecommerce.api.controller.v1.review;

import com.dev.core.ecommerce.api.controller.v1.response.ApiResponse;
import com.dev.core.ecommerce.api.controller.v1.review.request.CreateReviewRequest;
import com.dev.core.ecommerce.api.controller.v1.review.response.ReviewListResponse;
import com.dev.core.ecommerce.api.controller.v1.review.usecase.ReviewUseCase;
import com.dev.core.ecommerce.service.review.ReviewService;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.enums.review.ReviewTargetType;
import jakarta.validation.Valid;
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
    private final ReviewUseCase reviewUseCase;

    @PostMapping("/v1/reviews")
    public ApiResponse<Object> create(
            User user,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        reviewService.creat(user, request.toTarget(), request.toContent(), request.imageIds());
        return ApiResponse.success();
    }

    @GetMapping("/v1/reviews")
    public ApiResponse<ReviewListResponse> findReviews(
            @RequestParam ReviewTargetType targetType,
            @RequestParam Long targetId,
            Pageable pageable
    ) {
        var result = reviewUseCase.findReviews(targetType, targetId, pageable);
        return ApiResponse.success(result);
    }
}
