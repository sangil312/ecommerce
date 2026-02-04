package com.dev.core.ecommerce.domain.cart;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemTest {

    @Test
    void create() {
        CartItem cartItem = CartItem.create(1L, 1L, 1L);

        assertThat(cartItem).isNotNull();
        assertThat(cartItem.getProductId()).isEqualTo(1L);
        assertThat(cartItem.getQuantity()).isEqualTo(1L);
    }
}