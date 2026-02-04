package com.dev.core.ecommerce.api.controller.v1.order.usecase;

import com.dev.core.ecommerce.service.cart.CartService;
import com.dev.core.ecommerce.service.order.OrderService;
import com.dev.core.ecommerce.support.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class OrderUseCase {
    private final OrderService orderService;
    private final CartService cartService;

    public String createFromCart(User user, Collection<Long> cartItemIds) {
        var cart = cartService.findCart(user, cartItemIds);
        return orderService.createOrder(user, cart.toNewOrder());
    }
}
