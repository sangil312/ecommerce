package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.service.review.dto.ReviewTarget;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewValidator {
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    public String validateNewReview(User user, ReviewTarget target) {
        var reviewKeys = orderItemRepository.findRecentOrderItemByProduct(
                        user.id(),
                        target.id(),
                        OrderStatus.PAID,
                        LocalDateTime.now().minusDays(30),
                        EntityState.ACTIVE
                )
                .stream()
                .map(it -> "ORDER_ITEM_" + it.getId())
                .toList();

        var existReviewKeys = reviewRepository.findByUserIdAndReviewKeyIn(user.id(), reviewKeys)
                .stream()
                .map(Review::getReviewKey)
                .collect(Collectors.toSet());

        return reviewKeys.stream()
                .filter(it -> !existReviewKeys.contains(it))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorType.REVIEW_HAS_NOT_ORDER));
    }
}
