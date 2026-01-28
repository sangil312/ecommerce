package com.dev.core.ecommerce.service.product;

import com.dev.core.ecommerce.support.response.Page;
import com.dev.core.ecommerce.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductReader productReader;

    public Page<Product> findProducts(Long categoryId, Pageable pageable) {
        return productReader.findProductsByCategory(categoryId, pageable);
    }

    public Product findProduct(Long productId) {
        return productReader.findProduct(productId);
    }
}
