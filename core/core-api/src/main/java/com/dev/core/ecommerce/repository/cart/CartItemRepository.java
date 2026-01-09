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
