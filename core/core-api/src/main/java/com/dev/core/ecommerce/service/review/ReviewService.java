package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.service.review.dto.ReviewAndImage;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.support.response.Page;
import com.dev.core.ecommerce.service.review.dto.ReviewContent;
import com.dev.core.ecommerce.service.review.dto.ReviewTarget;
import com.dev.core.ecommerce.service.review.dto.RateSummary;
import com.dev.core.enums.review.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewValidator reviewValidator;
    private final ReviewWriter reviewWriter;
    private final ReviewReader reviewReader;

    public void creat(User user, ReviewTarget target, ReviewContent content, List<Long> imageIds) {
        String reviewKey = reviewValidator.validateNewReview(user, target);
        reviewWriter.createReview(user, reviewKey, target, content, imageIds);
    }

    public Page<ReviewAndImage> findReviews(ReviewTargetType targetType, Long targetId, Pageable pageable) {
        return reviewReader.findReviewsByTargetType(targetType, targetId, pageable);
    }

    public RateSummary findReviewRateSummary(ReviewTargetType targetType, Long targetId) {
        return reviewReader.findRateSummary(targetType, targetId);
    }

    public Map<Long, RateSummary> findReviewsRateSummary(
            ReviewTargetType targetType,
            Collection<Long> targetIds
    ) {
        return reviewReader.findReviewsRateSummary(targetType, targetIds);
    }
}
