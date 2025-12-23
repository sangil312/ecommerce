package com.dev.ecommerce.repository.cart;

import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserIdAndState(Long userId, EntityState state);

    @Query("""
            SELECT DISTINCT ci
            FROM CartItem ci
                JOIN FETCH ci.product p
            WHERE ci.userId = :userId
                AND ci.id IN :cartItemIds
                AND ci.state = :state
                AND p.state = :state
    """)
    List<CartItem> findWithProduct(Long userId, Collection<Long> cartItemIds, EntityState state);
}
