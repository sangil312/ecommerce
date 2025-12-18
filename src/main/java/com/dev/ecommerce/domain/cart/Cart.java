package com.dev.ecommerce.domain.cart;

import lombok.Getter;

import java.util.List;

@Getter
public class Cart {
    private Long userId;
    private List<CartItem> items;

    public Cart create(Long userId, List<CartItem> items) {
        Cart cart = new Cart();
        cart.userId = userId;
        cart.items = items;
        return cart;
    }
}
