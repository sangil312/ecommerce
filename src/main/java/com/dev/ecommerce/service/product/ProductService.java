package com.dev.ecommerce.service.product;

import com.dev.ecommerce.domain.product.Product;
import com.dev.ecommerce.domain.product.ProductCategory;
import com.dev.ecommerce.domain.product.ProductCategoryRepository;
import com.dev.ecommerce.domain.product.ProductRepository;
import com.dev.ecommerce.common.response.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public Page<Product> findProductsByCategory(Long categoryId, Pageable pageable) {
        var productCategories = productCategoryRepository.findByCategoryId(categoryId, pageable);
        var productIds = productCategories.getContent().stream().map(ProductCategory::getProductId).toList();
        var products = productRepository.findAllById(productIds);

        return Page.of(products, productCategories.hasNext());
    }
}
