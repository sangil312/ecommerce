package com.dev.ecommerce.service.cart;

import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.domain.cart.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class CartService {
    private final CartReader cartReader;

    public Cart find(User user, Collection<Long> cartItemIds) {
        return cartReader.find(user, cartItemIds);
    }
}
