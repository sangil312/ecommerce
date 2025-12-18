package com.dev.ecommerce.repository.cart;

import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserIdAndState(Long userId, EntityState state);
}
