package com.dev.ecommerce.service.cart;

import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.domain.cart.Cart;
import com.dev.ecommerce.domain.cart.CartItem;
import com.dev.ecommerce.repository.cart.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@Component
@RequiredArgsConstructor
public class CartReader {
    private final CartItemRepository cartItemRepository;

    public Cart find(User user, Collection<Long> cartItemIds) {
        List<CartItem> cartItems = cartItemRepository.findWithProduct(user.id(), cartItemIds, EntityState.ACTIVE);
        return Cart.of(user.id(), cartItems);
    }
}
