package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.common.error.ApiException;
import com.dev.core.ecommerce.common.error.ErrorType;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.ecommerce.repository.product.ProductRepository;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.service.product.ProductBuilder;
import com.dev.core.ecommerce.service.review.request.ReviewTarget;
import com.dev.core.enums.review.ReviewTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ReviewValidatorTest extends IntegrationTestSupport {
    @Autowired
    private ReviewValidator reviewValidator;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User(1L);
        testProduct = new ProductBuilder().name("상품1").price(BigDecimal.valueOf(1_000)).build();
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @DisplayName("리뷰 생성 검증: 주문 내역이 있으면 리뷰 KEY를 반환한다")
    void validateNewReview() {
        // given
        OrderItem orderItem = createPaidOrderItem(testUser.id(), testProduct);
        ReviewTarget target = new ReviewTarget(ReviewTargetType.PRODUCT, testProduct.getId());

        // when
        String reviewKey = reviewValidator.validateNewReview(testUser, target);

        // then
        assertThat(reviewKey).isEqualTo("ORDER_ITEM_" + orderItem.getId());
    }

    @Test
    @DisplayName("리뷰 생성 검증: 주문 내역이 없으면 예외가 발생한다")
    void validateNewReviewWithNoOrder() {
        // given
        ReviewTarget target = new ReviewTarget(ReviewTargetType.PRODUCT, testProduct.getId());

        // when then
        assertThatThrownBy(() -> reviewValidator.validateNewReview(testUser, target))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.REVIEW_HAS_NOT_ORDER);
    }

    @Test
    @DisplayName("리뷰 생성 검증: 이미 리뷰가 있으면 예외가 발생한다")
    void validateNewReviewWithExistingReview() {
        // given
        OrderItem orderItem = createPaidOrderItem(testUser.id(), testProduct);
        String reviewKey = "ORDER_ITEM_" + orderItem.getId();
        ReviewTarget target = new ReviewTarget(ReviewTargetType.PRODUCT, testProduct.getId());
        reviewRepository.save(
                Review.create(
                        testUser.id(),
                        reviewKey,
                        target.targetType(),
                        target.id(),
                        BigDecimal.valueOf(4.5),
                        "좋아요"
                )
        );

        // when then
        assertThatThrownBy(() -> reviewValidator.validateNewReview(testUser, target))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.REVIEW_HAS_NOT_ORDER);
    }

    private OrderItem createPaidOrderItem(Long userId, Product product) {
        Order order = Order.create(userId, product.getPrice());
        order.paid();
        order = orderRepository.save(order);

        OrderItem orderItem = OrderItem.create(
                order.getId(),
                product.getId(),
                1L,
                product.getName(),
                product.getPrice(),
                product.getPrice()
        );
        return orderItemRepository.save(orderItem);
    }
}
