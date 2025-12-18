package com.dev.ecommerce.service.cart;

import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.domain.User;
import com.dev.ecommerce.domain.cart.Cart;
import com.dev.ecommerce.domain.cart.CartItem;
import com.dev.ecommerce.repository.cart.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;

    public Cart findCart(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdAndState(user.id(), EntityState.ACTIVE);

        return null;
    }
}
