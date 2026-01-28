package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.support.response.Page;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.repository.review.response.ReviewRateSummary;
import com.dev.core.ecommerce.service.review.response.RateSummary;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.review.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class ReviewReader {
    private final ReviewRepository reviewRepository;

    public Page<Review> findReviewsByTargetType(
            ReviewTargetType targetType,
            Long targetId,
            Pageable pageable
    ) {
        var reviews = reviewRepository.findByTargetIdAndTargetTypeAndState(
                targetId, targetType, EntityState.ACTIVE, pageable);

        return Page.of(reviews.getContent(), reviews.hasNext());
    }

    public RateSummary findRateSummary(ReviewTargetType targetType, Long targetId) {
        return reviewRepository.findRateSummary(targetId, targetType, EntityState.ACTIVE);
    }

    public Map<Long, RateSummary> findReviewsRateSummary(
            ReviewTargetType targetType,
            Collection<Long> targetIds
    ) {
        var summaries = reviewRepository.findReviewsRateSummary(targetIds, targetType, EntityState.ACTIVE);

        return summaries.stream()
                .collect(toMap(ReviewRateSummary::targetId, RateSummary::of));
    }
}
