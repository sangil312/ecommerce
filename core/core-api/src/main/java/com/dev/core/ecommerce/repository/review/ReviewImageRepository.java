package com.dev.core.ecommerce.repository.review;

import com.dev.core.ecommerce.domain.review.ReviewImage;
import com.dev.core.enums.EntityState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReviewIdInAndState(Collection<Long> reviewIds, EntityState state);
}
