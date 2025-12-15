package com.dev.ecommerce.controller.product;

import com.dev.ecommerce.controller.product.response.ProductResponse;
import com.dev.ecommerce.controller.response.ApiResponse;
import com.dev.ecommerce.controller.response.PageResponse;
import com.dev.ecommerce.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/api/products")
    public ApiResponse<PageResponse<ProductResponse>> findProduct(
            @RequestParam Long categoryId,
            Pageable pageable
    ) {
        var result = productService.findProductsByCategory(categoryId, pageable);
        return ApiResponse.success(PageResponse.of(ProductResponse.of(result.contents()), result.hasNext()));
    }
}
