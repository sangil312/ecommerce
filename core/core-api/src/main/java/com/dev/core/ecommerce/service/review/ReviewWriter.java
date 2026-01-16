package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.service.review.request.ReviewContent;
import com.dev.core.ecommerce.service.review.request.ReviewTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReviewWriter {
    private final ReviewRepository reviewRepository;

    @Transactional
    public void createReview(User user, String reviewKey, ReviewTarget target, ReviewContent content) {
        reviewRepository.save(
                Review.create(
                        user.id(),
                        reviewKey,
                        target.targetType(),
                        target.id(),
                        content.rate(),
                        content.content()
                )
        );
    }
}
