package com.dev.core.ecommerce.repository.cart;

import com.dev.core.ecommerce.domain.cart.CartItem;
import com.dev.core.enums.EntityState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserIdAndState(Long userId, EntityState state);

    @Query("""
            SELECT DISTINCT cartItem
            FROM CartItem cartItem
                JOIN FETCH cartItem.product product
            WHERE cartItem.userId = :userId
                AND cartItem.id IN :cartItemIds
                AND cartItem.state = :state
                AND product.state = :state
    """)
    List<CartItem> findWithProduct(Long userId, Collection<Long> cartItemIds, EntityState state);
}
