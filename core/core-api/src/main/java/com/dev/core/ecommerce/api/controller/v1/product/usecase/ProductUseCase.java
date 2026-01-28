package com.dev.core.ecommerce.api.controller.v1.product.usecase;

import com.dev.core.ecommerce.api.controller.v1.product.response.ProductResponse;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.service.product.ProductService;
import com.dev.core.ecommerce.service.review.ReviewService;
import com.dev.core.ecommerce.support.response.Page;
import com.dev.core.enums.review.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProductUseCase {
    private final ProductService productService;
    private final ReviewService reviewService;

    public Page<ProductResponse> findProducts(Long categoryId, Pageable pageable) {
        var productPages = productService.findProducts(categoryId, pageable);
        var productIds = productPages.contents().stream().map(Product::getId).toList();

        var reviewsRateSummary = reviewService.findReviewsRateSummary(ReviewTargetType.PRODUCT, productIds);

        var productResponses = ProductResponse.of(productPages.contents(), reviewsRateSummary);

        return Page.of(productResponses, productPages.hasNext());
    }
}
