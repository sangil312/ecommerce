package com.dev.core.ecommerce.service.review;

import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.repository.review.ReviewRepository;
import com.dev.core.ecommerce.service.review.dto.ReviewAndImage;
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
    @DisplayName("리뷰 조회: 대상 타입과 ID로 조회")
    void findReviewsByTargetType() {
        // given
        Review review1 = Review.create(1L, "ORDER_ITEM_1", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(4.5), "good");
        Review review2 = Review.create(2L, "ORDER_ITEM_2", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(3.0), "ok");
        Review deletedReview = Review.create(3L, "ORDER_ITEM_3", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(2.0), "bad");
        deletedReview.delete();

        reviewRepository.saveAll(List.of(review1, review2, deletedReview));

        // when
        Page<ReviewAndImage> result = reviewReader.findReviewsByTargetType(
                ReviewTargetType.PRODUCT, 1L, PageRequest.of(0, 2));

        // then
        assertThat(result.contents()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.contents())
                .extracting(
                        ReviewAndImage::userId,
                        ReviewAndImage::reviewKey,
                        it -> it.target().targetType(),
                        it -> it.target().id(),
                        it -> it.content().rate(),
                        it -> it.content().content(),
                        ReviewAndImage::images
                )
                .containsExactlyInAnyOrder(
                        tuple(1L, "ORDER_ITEM_1", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(4.5), "good", List.of()),
                        tuple(2L, "ORDER_ITEM_2", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(3.0), "ok", List.of())
                );
    }

    @Test
    @DisplayName("리뷰 수, 평점 조회: 대상 타입과 ID로 조회")
    void findRateSummary() {
        // given
        Review review1 = Review.create(1L, "ORDER_ITEM_1", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(4.0), "good");
        Review review2 = Review.create(2L, "ORDER_ITEM_2", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(2.0), "ok");
        Review deletedReview = Review.create(3L, "ORDER_ITEM_3", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(1.0), "bad");
        deletedReview.delete();
        reviewRepository.saveAll(List.of(review1, review2, deletedReview));

        // when
        var result = reviewReader.findRateSummary(ReviewTargetType.PRODUCT, 1L);

        // then
        assertThat(result.count()).isEqualTo(2L);
        assertThat(result.rate()).isEqualByComparingTo(BigDecimal.valueOf(3.0));
    }

    @Test
    @DisplayName("리뷰 수, 평점 목록 조회: 대상 타입과 대상 ID 목록으로 조회")
    void findReviewsRateSummary() {
        // given
        Review review1 = Review.create(1L, "ORDER_ITEM_1", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(4.0), "good");
        Review review2 = Review.create(2L, "ORDER_ITEM_2", ReviewTargetType.PRODUCT, 1L, BigDecimal.valueOf(2.0), "ok");
        Review review3 = Review.create(3L, "ORDER_ITEM_3", ReviewTargetType.PRODUCT, 2L, BigDecimal.valueOf(5.0), "great");
        Review deletedReview = Review.create(4L, "ORDER_ITEM_4", ReviewTargetType.PRODUCT, 2L, BigDecimal.valueOf(1.0), "bad");
        deletedReview.delete();
        reviewRepository.saveAll(List.of(review1, review2, review3, deletedReview));

        // when
        var result = reviewReader.findReviewsRateSummary(ReviewTargetType.PRODUCT, List.of(1L, 2L));

        // then
        assertThat(result).containsKeys(1L, 2L);
        assertThat(result.get(1L).count()).isEqualTo(2L);
        assertThat(result.get(1L).rate()).isEqualByComparingTo(BigDecimal.valueOf(3.0));
        assertThat(result.get(2L).count()).isEqualTo(1L);
        assertThat(result.get(2L).rate()).isEqualByComparingTo(BigDecimal.valueOf(5.0));
    }

}
