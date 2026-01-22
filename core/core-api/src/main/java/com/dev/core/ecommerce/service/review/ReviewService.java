package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.service.review.request.ReviewContent;
import com.dev.core.ecommerce.service.review.request.ReviewTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewValidator reviewValidator;
    private final ReviewWriter reviewWriter;

    public void creat(User user, ReviewTarget target, ReviewContent content) {
        String reviewKey = reviewValidator.validateNewReview(user, target);
        reviewWriter.createReview(user, reviewKey, target, content);
    }
}
