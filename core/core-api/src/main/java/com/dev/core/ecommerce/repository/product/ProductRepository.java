package com.dev.core.ecommerce.repository.product;

import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.enums.EntityState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndState(Long productId, EntityState state);
    List<Product> findByIdInAndState(Collection<Long> productIds, EntityState state);
}
