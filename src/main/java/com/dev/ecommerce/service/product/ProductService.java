package com.dev.ecommerce.service.product;

import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.common.error.ApiException;
import com.dev.ecommerce.common.error.ErrorType;
import com.dev.ecommerce.domain.product.Product;
import com.dev.ecommerce.domain.product.ProductCategory;
import com.dev.ecommerce.repository.product.ProductCategoryRepository;
import com.dev.ecommerce.repository.product.ProductRepository;
import com.dev.ecommerce.common.response.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public Page<Product> findProductsByCategory(Long categoryId, Pageable pageable) {
        Slice<ProductCategory> productCategories =
                productCategoryRepository.findByCategoryIdAndState(categoryId, EntityState.ACTIVE, pageable);
        List<Long> productIds = productCategories.getContent().stream().map(ProductCategory::getProductId).toList();
        List<Product> products = productRepository.findAllById(productIds);

        return Page.of(products, productCategories.hasNext());
    }

    public Product findProduct(Long productId) {
        return productRepository.findByIdAndState(productId, EntityState.ACTIVE)
                .orElseThrow(() -> new ApiException(ErrorType.PRODUCT_NOT_FOUND));
    }
}
