package com.dev.ecommerce.domain.product;

import com.dev.ecommerce.common.EntityState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndState(Long productId, EntityState state);
}
