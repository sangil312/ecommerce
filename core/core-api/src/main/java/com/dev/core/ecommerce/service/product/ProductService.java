package com.dev.core.ecommerce.service.product;

import com.dev.core.ecommerce.common.response.Page;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.service.review.ReviewReader;
import com.dev.core.enums.review.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductReader productReader;
    private final ReviewReader reviewReader;

    public Page<Product> findProducts(Long categoryId, Pageable pageable) {
        return productReader.findProductsByCategory(categoryId, pageable);
    }

    public Product findProduct(Long productId) {
        return productReader.findProduct(productId);
    }

//    public Page<ProductWithRateSummary> findProductsWithRateSummary(Long categoryId, Pageable pageable) {
//        Page<Product> products = productReader.findProductsByCategory(categoryId, pageable);
//        var productIds = products.contents().stream().map(Product::getId).toList();
//        var rateSummaries = reviewReader.findRateSummaries(ReviewTargetType.PRODUCT, productIds);
//
//        var items = products.contents().stream()
//                .map(product -> new ProductWithRateSummary(
//                        product,
//                        rateSummaries.get(product.getId())
//                ))
//                .toList();
//
//        return Page.of(items, products.hasNext());
//    }
}
