package com.dev.core.ecommerce.repository.review;

import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.review.dto.RateSummaryView;
import com.dev.core.ecommerce.repository.review.dto.RateSummaryGroupView;
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
        SELECT new com.dev.core.ecommerce.repository.review.dto.RateSummaryView(
            COUNT(review.id),
            AVG(review.rate)
        )
        FROM Review review
        WHERE review.targetId = :targetId
            AND review.targetType = :targetType
            AND review.state = :state
    """)
    RateSummaryView findRateSummary(Long targetId, ReviewTargetType targetType, EntityState state);

    @Query("""
        SELECT new com.dev.core.ecommerce.repository.review.dto.RateSummaryGroupView(
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
    List<RateSummaryGroupView> findRateSummaryGroup(
            Collection<Long> targetIds,
            ReviewTargetType targetType,
            EntityState state
    );
}
