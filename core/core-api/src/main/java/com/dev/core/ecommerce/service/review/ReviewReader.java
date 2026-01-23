package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.common.response.Page;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.review.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewReader {
    private final ReviewRepository reviewRepository;

    public Page<Review> findReviewsByTargetType(
            ReviewTargetType targetType,
            Long targetId,
            Pageable pageable
    ) {
        Slice<Review> reviews = reviewRepository.findByTargetIdAndTargetTypeAndState(
                targetId, targetType, EntityState.ACTIVE, pageable);

        return Page.of(reviews.getContent(), reviews.hasNext());
    }
}
