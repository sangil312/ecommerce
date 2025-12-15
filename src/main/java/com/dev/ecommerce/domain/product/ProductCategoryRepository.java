package com.dev.ecommerce.domain.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Slice<ProductCategory> findByCategoryId(Long categoryId, Pageable pageable);
}
