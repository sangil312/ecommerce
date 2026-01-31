package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.service.review.dto.ReviewContent;
import com.dev.core.ecommerce.service.review.dto.ReviewTarget;
import com.dev.core.enums.review.ReviewTargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ReviewWriterTest extends IntegrationTestSupport {
    @Autowired
    private ReviewWriter reviewWriter;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 생성")
    void createReview() {
        // given
        User user = new User(1L);
        String reviewKey = "ORDER_ITEM_1";
        ReviewTarget target = ReviewTarget.of(ReviewTargetType.PRODUCT, 10L);
        ReviewContent content = ReviewContent.of(BigDecimal.valueOf(4_500, 3), "좋아요");

        // when
        reviewWriter.createReview(user, reviewKey, target, content);

        // then
        List<Review> reviews = reviewRepository.findByUserIdAndReviewKeyIn(user.id(), List.of(reviewKey));
        assertThat(reviews).hasSize(1);

        Review savedReview = reviews.getFirst();
        assertThat(savedReview.getUserId()).isEqualTo(user.id());
        assertThat(savedReview.getReviewKey()).isEqualTo(reviewKey);
        assertThat(savedReview.getTargetType()).isEqualTo(target.targetType());
        assertThat(savedReview.getTargetId()).isEqualTo(target.id());
        assertThat(savedReview.getRate()).isEqualByComparingTo(content.rate());
        assertThat(savedReview.getContent()).isEqualTo(content.content());
    }
}
