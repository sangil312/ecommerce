package com.dev.core.ecommerce.api.controller.v1.product;

import com.dev.core.ecommerce.api.controller.v1.product.response.ProductDetailResponse;
import com.dev.core.ecommerce.api.controller.v1.product.response.ProductResponse;
import com.dev.core.ecommerce.api.controller.v1.product.usecase.ProductUseCase;
import com.dev.core.ecommerce.api.controller.v1.response.ApiResponse;
import com.dev.core.ecommerce.api.controller.v1.response.PageResponse;
import com.dev.core.ecommerce.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductUseCase productUseCase;

    @GetMapping("/v1/products")
    public ApiResponse<PageResponse<ProductResponse>> findProduct(
            @RequestParam Long categoryId,
            Pageable pageable
    ) {
        var result = productUseCase.findProducts(categoryId, pageable);
        return ApiResponse.success(PageResponse.of(result.contents(), result.hasNext()));
    }

    @GetMapping("/v1/products/{productId}")
    public ApiResponse<ProductDetailResponse> findProduct(
            @PathVariable Long productId
    ) {
        var result = productService.findProduct(productId);
        return ApiResponse.success(ProductDetailResponse.of(result));
    }
}
