package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.support.response.Page;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.service.review.request.ReviewContent;
import com.dev.core.ecommerce.service.review.request.ReviewTarget;
import com.dev.core.enums.review.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewValidator reviewValidator;
    private final ReviewWriter reviewWriter;
    private final ReviewReader reviewReader;

    public void creat(User user, ReviewTarget target, ReviewContent content) {
        String reviewKey = reviewValidator.validateNewReview(user, target);
        reviewWriter.createReview(user, reviewKey, target, content);
    }

    public Page<Review> findReviews(ReviewTargetType targetType, Long targetId, Pageable pageable) {
        return reviewReader.findReviewsByTargetType(targetType, targetId, pageable);
    }
}
