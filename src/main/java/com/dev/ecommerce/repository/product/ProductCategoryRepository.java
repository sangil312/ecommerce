package com.dev.ecommerce.repository.product;

import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.domain.product.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Slice<ProductCategory> findByCategoryIdAndState(Long categoryId, EntityState state, Pageable pageable);
}
