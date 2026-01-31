package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.domain.review.ReviewImage;
import com.dev.core.ecommerce.repository.review.ReviewImageRepository;
import com.dev.core.ecommerce.service.review.dto.ReviewAndImage;
import com.dev.core.ecommerce.support.response.Page;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.repository.review.dto.RateSummaryGroupView;
import com.dev.core.ecommerce.service.review.dto.RateSummary;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.review.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class ReviewReader {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    public Page<ReviewAndImage> findReviewsByTargetType(
            ReviewTargetType targetType,
            Long targetId,
            Pageable pageable
    ) {
        var reviews = reviewRepository.findByTargetIdAndTargetTypeAndState(
                targetId, targetType, EntityState.ACTIVE, pageable);

        var reviewIds = reviews.getContent().stream().map(Review::getId).toList();

        if (reviewIds.isEmpty()) {
            return Page.empty();
        }

        var reviewImages = reviewImageRepository.findByReviewIdInAndState(reviewIds, EntityState.ACTIVE);

        Map<Long, List<ReviewImage>> reviewImageMap = reviewImages.stream()
                .collect(groupingBy(ReviewImage::getReviewId));

        var reviewAndImages = reviews.getContent().stream()
                .map(it -> ReviewAndImage.of(it, reviewImageMap.getOrDefault(it.getId(), List.of())))
                .toList();

        return Page.of(reviewAndImages, reviews.hasNext());
    }

    public RateSummary findRateSummary(ReviewTargetType targetType, Long targetId) {
        var rateSummary = reviewRepository.findRateSummary(targetId, targetType, EntityState.ACTIVE);

        return RateSummary.of(rateSummary);
    }

    public Map<Long, RateSummary> findReviewsRateSummary(
            ReviewTargetType targetType,
            Collection<Long> targetIds
    ) {
        var rateSummaries = reviewRepository.findRateSummaryGroup(targetIds, targetType, EntityState.ACTIVE);

        return rateSummaries.stream()
                .collect(toMap(RateSummaryGroupView::targetId, RateSummary::of));
    }
}
