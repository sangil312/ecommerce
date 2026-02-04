package com.dev.core.ecommerce.service.cart;

import com.dev.core.ecommerce.repository.cart.CartItemRepository;
import com.dev.core.ecommerce.support.BaseEntity;
import com.dev.core.enums.EntityState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class CartWriter {
    private final CartItemRepository cartItemRepository;

    @Transactional
    public void deleteCartItemsByProduct(Long userId, Collection<Long> productIds) {
        cartItemRepository.findByUserIdAndProductIdInAndState(userId, productIds, EntityState.ACTIVE)
                .forEach(BaseEntity::delete);
    }
}
