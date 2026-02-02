package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.domain.review.ReviewImage;
import com.dev.core.ecommerce.repository.file.ImageRepository;
import com.dev.core.ecommerce.repository.review.ReviewImageRepository;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.service.review.dto.ReviewContent;
import com.dev.core.ecommerce.service.review.dto.ReviewTarget;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewWriter {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public void createReview(
            User user,
            String reviewKey,
            ReviewTarget target,
            ReviewContent content,
            List<Long> imageIds
    ) {
        var savedReview = reviewRepository.save(
                Review.create(
                        user.id(),
                        reviewKey,
                        target.targetType(),
                        target.id(),
                        content.rate(),
                        content.content()
                )
        );

        var uploadedImages = imageRepository.findByUserIdAndIdIn(savedReview.getUserId(), imageIds);

        if (imageIds.size() != uploadedImages.size()) throw new ApiException(ErrorType.INVALID_REQUEST);

        reviewImageRepository.saveAll(
                uploadedImages.stream()
                        .map(it ->
                                ReviewImage.create(
                                        savedReview.getUserId(),
                                        savedReview.getId(),
                                        it.getId(),
                                        it.getImageUrl()
                                )
                        )
                        .toList()
        );
    }
}
