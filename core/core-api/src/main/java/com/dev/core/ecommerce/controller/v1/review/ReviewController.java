package com.dev.core.ecommerce.controller.v1.review;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.controller.v1.response.ApiResponse;
import com.dev.core.ecommerce.controller.v1.review.request.CreateReviewRequest;
import com.dev.core.ecommerce.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}
