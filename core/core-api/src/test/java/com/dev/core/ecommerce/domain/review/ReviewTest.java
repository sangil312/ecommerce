package com.dev.core.ecommerce.domain.review;

import com.dev.core.enums.review.ReviewTargetType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewTest {

    @Test
    void create() {
        ReviewTargetType targetType = ReviewTargetType.PRODUCT;
        Long targetId = 10L;
        BigDecimal rate = BigDecimal.valueOf(4_500, 3);
        String content = "좋아요";

        Review review = Review.create(1L, "ORDER_ITEM_1", targetType, targetId, rate, content);

        assertThat(review.getUserId()).isEqualTo(1L);
        assertThat(review.getReviewKey()).isEqualTo("ORDER_ITEM_1");
        assertThat(review.getTargetType()).isEqualTo(targetType);
        assertThat(review.getTargetId()).isEqualTo(targetId);
        assertThat(review.getRate()).isEqualByComparingTo(rate);
        assertThat(review.getContent()).isEqualTo(content);
    }
}
