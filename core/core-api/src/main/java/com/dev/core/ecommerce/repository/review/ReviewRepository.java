package com.dev.core.ecommerce.repository.review;

import com.dev.core.ecommerce.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserIdAndReviewKeyIn(Long userId, Collection<String> reviewKeys);
}
