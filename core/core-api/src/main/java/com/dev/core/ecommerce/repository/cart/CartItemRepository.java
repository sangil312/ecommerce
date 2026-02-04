package com.dev.core.ecommerce.repository.cart;

import com.dev.core.ecommerce.domain.cart.CartItem;
import com.dev.core.enums.EntityState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserIdAndIdInAndState(Long userId, Collection<Long> cartItemIds, EntityState state);
    List<CartItem> findByUserIdAndProductIdInAndState(Long userId, Collection<Long> productIds, EntityState state);
}
