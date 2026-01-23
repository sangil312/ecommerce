package com.dev.core.ecommerce.repository.review;

import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.review.ReviewTargetType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
