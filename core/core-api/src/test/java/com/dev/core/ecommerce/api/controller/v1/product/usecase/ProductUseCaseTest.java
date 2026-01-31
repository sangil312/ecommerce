package com.dev.core.ecommerce.api.controller.v1.product.usecase;

import com.dev.core.ecommerce.api.controller.v1.product.response.ProductDetailResponse;
import com.dev.core.ecommerce.api.controller.v1.product.response.ProductResponse;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.service.product.ProductService;
import com.dev.core.ecommerce.service.review.ReviewService;
import com.dev.core.ecommerce.service.review.dto.RateSummary;
import com.dev.core.ecommerce.support.response.Page;
import com.dev.core.enums.review.ReviewTargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductService productService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ProductUseCase productUseCase;

    @Test
    @DisplayName("상품 목록 조회: 상품과 리뷰 수, 평점을 조합한다")
    void findProducts() {
        // given
        Long categoryId = 10L;
        PageRequest pageable = PageRequest.of(0, 2);

        Product product1 = Product.create("상품1", "https://~1", "본문1", "요약1", BigDecimal.valueOf(1_000));
        Product product2 = Product.create("상품2", "https://~2", "본문2", "요약2", BigDecimal.valueOf(2_000));
        ReflectionTestUtils.setField(product1, "id", 1L);
        ReflectionTestUtils.setField(product2, "id", 2L);

        Page<Product> productPage = Page.of(List.of(product1, product2), true);
        Map<Long, RateSummary> reviewsRateSummary = Map.of(
                1L, new RateSummary(3L, BigDecimal.valueOf(4.5)),
                2L, new RateSummary(1L, BigDecimal.valueOf(5.0))
        );

        when(productService.findProducts(categoryId, pageable)).thenReturn(productPage);
        when(reviewService.findReviewsRateSummary(any(ReviewTargetType.class), anyList()))
                .thenReturn(reviewsRateSummary);

        // when
        Page<ProductResponse> result = productUseCase.findProducts(categoryId, pageable);

        // then
        assertThat(result.hasNext()).isTrue();
        assertThat(result.contents())
                .extracting(
                        ProductResponse::productId,
                        ProductResponse::name,
                        ProductResponse::thumbnailUrl,
                        ProductResponse::shortDescription,
                        ProductResponse::price,
                        ProductResponse::rate,
                        ProductResponse::rateCount
                )
                .containsExactly(
                        tuple(1L, "상품1", "https://~1", "요약1", BigDecimal.valueOf(1_000), BigDecimal.valueOf(4.5), 3L),
                        tuple(2L, "상품2", "https://~2", "요약2", BigDecimal.valueOf(2_000), BigDecimal.valueOf(5.0), 1L)
                );
        verify(productService).findProducts(categoryId, pageable);
        verify(reviewService).findReviewsRateSummary(ReviewTargetType.PRODUCT, List.of(1L, 2L));
    }

    @Test
    @DisplayName("상품 상세 조회: 상품과 리뷰 수, 평점을 조합한다")
    void findProduct() {
        // given
        Long productId = 100L;
        Product product = Product.create("상품1", "https://~1", "본문1", "요약1", BigDecimal.valueOf(1_000));
        ReflectionTestUtils.setField(product, "id", productId);
        RateSummary rateSummary = new RateSummary(0L, null);

        when(productService.findProduct(productId)).thenReturn(product);
        when(reviewService.findReviewRateSummary(ReviewTargetType.PRODUCT, productId)).thenReturn(rateSummary);

        // when
        ProductDetailResponse result = productUseCase.findProduct(productId);

        // then
        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.name()).isEqualTo("상품1");
        assertThat(result.shortDescription()).isEqualTo("요약1");
        assertThat(result.rate()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.rateCount()).isEqualTo(0L);
        verify(productService).findProduct(productId);
        verify(reviewService).findReviewRateSummary(ReviewTargetType.PRODUCT, productId);
    }
}
