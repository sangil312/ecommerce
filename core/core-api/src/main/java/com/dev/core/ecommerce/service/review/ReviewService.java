package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.service.review.request.ReviewContent;
import com.dev.core.ecommerce.service.review.request.ReviewTarget;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    public void creatReview(User user, ReviewTarget target, ReviewContent content) {
        List<String> reviewKeys = orderItemRepository.findRecentOrderItemByProduct(
                        user.id(),
                        target.targetId(),
                        OrderStatus.PAID,
                        LocalDateTime.now().minusDays(30),
                        EntityState.ACTIVE
                )
                .stream()
                .map(it -> "ORDER_ITEM_" + it.getId())
                .toList();

        reviewRepository.findByUserIdAndReviewKeyIn(user.id(), reviewKeys);
    }
}
