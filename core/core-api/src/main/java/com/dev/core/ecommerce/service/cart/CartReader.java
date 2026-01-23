package com.dev.core.ecommerce.service.cart;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.domain.cart.Cart;
import com.dev.core.ecommerce.domain.cart.CartItem;
import com.dev.core.ecommerce.repository.cart.CartItemRepository;
import com.dev.core.enums.EntityState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@Component
@RequiredArgsConstructor
public class CartReader {
    private final CartItemRepository cartItemRepository;

    public Cart findCart(User user, Collection<Long> cartItemIds) {
        List<CartItem> cartItems = cartItemRepository.findWithProduct(user.id(), cartItemIds, EntityState.ACTIVE);
        return Cart.of(user.id(), cartItems);
    }
}
