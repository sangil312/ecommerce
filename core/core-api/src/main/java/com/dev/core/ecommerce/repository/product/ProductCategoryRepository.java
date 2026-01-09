package com.dev.core.ecommerce.repository.product;

import com.dev.core.ecommerce.domain.product.ProductCategory;
import com.dev.core.enums.EntityState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Slice<ProductCategory> findByCategoryIdAndState(Long categoryId, EntityState state, Pageable pageable);
}
