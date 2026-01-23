package com.dev.core.ecommerce.service.cart;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.domain.cart.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class CartService {
    private final CartReader cartReader;

    public Cart find(User user, Collection<Long> cartItemIds) {
        return cartReader.findCart(user, cartItemIds);
    }
}
