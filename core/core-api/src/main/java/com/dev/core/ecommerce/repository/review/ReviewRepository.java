package com.dev.core.ecommerce.repository.review;

import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.service.review.response.RateSummary;
import com.dev.core.ecommerce.repository.review.response.ReviewRateSummary;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.review.ReviewTargetType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserIdAndReviewKeyIn(Long userId, Collection<String> reviewKeys);

    Slice<Review> findByTargetIdAndTargetTypeAndState(
            Long targetId,
            ReviewTargetType targetType,
            EntityState state,
            Pageable pageable
    );

    @Query("""
        SELECT
            COUNT(review.id) AS count,
            AVG(review.rate) AS rate
        FROM Review review
        WHERE review.targetId = :targetId
            AND review.targetType = :targetType
            AND review.state = :state
    """)
    RateSummary findRateSummary(Long targetId, ReviewTargetType targetType, EntityState state);

    @Query("""
        SELECT new com.dev.core.ecommerce.repository.review.response.ReviewRateSummary(
            review.targetId,
            COUNT(review.id),
            AVG(review.rate)
        )
        FROM Review review
        WHERE review.targetId in :targetIds
            AND review.targetType = :targetType
            AND review.state = :state
        GROUP BY review.targetId
    """)
    List<ReviewRateSummary> findReviewsRateSummary(
            Collection<Long> targetIds,
            ReviewTargetType targetType,
            EntityState state
    );
}
