package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.support.response.Page;
import com.dev.core.enums.review.ReviewTargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class ReviewReaderTest extends IntegrationTestSupport {

    @Autowired
    private ReviewReader reviewReader;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 조회: 리뷰 대상 타입과 리뷰 대상 ID로 조회")
    void findReviewsByTargetType() {
        // given
        Review review1 = Review.create(1L, "ORDER_ITEM_1", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(4.5), "good");
        Review review2 = Review.create(2L, "ORDER_ITEM_2", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(3.0), "ok");
        Review deletedReview = Review.create(3L, "ORDER_ITEM_3", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(2.0), "bad");
        deletedReview.delete();

        reviewRepository.saveAll(List.of(review1, review2, deletedReview));

        // when
        Page<Review> result = reviewReader.findReviewsByTargetType(
                ReviewTargetType.PRODUCT, 1L, PageRequest.of(0, 2));

        // then
        assertThat(result.contents()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.contents())
                .extracting(
                        Review::getUserId,
                        Review::getReviewKey,
                        Review::getTargetType,
                        Review::getTargetId,
                        Review::getRate,
                        Review::getContent
                )
                .containsExactlyInAnyOrder(
                        tuple(1L, "ORDER_ITEM_1", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(4.5), "good"),
                        tuple(2L, "ORDER_ITEM_2", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(3.0), "ok")
                );
    }
}
