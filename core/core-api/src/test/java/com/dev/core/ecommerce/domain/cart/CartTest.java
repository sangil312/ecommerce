package com.dev.core.ecommerce.domain.cart;

import com.dev.core.ecommerce.service.order.request.NewOrder;
import com.dev.core.ecommerce.service.product.ProductBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class CartTest {

    @Test
    void of() {
        CartItem cartItem1 = CartItem.create(1L, new ProductBuilder().build(), 1L);
        CartItem cartItem2 = CartItem.create(1L, new ProductBuilder().build(), 1L);

        Cart cart = Cart.of(1L, List.of(cartItem1, cartItem2));

        assertThat(cart.userId()).isEqualTo(1L);
        assertThat(cart.items()).hasSize(2);
    }

    @Test
    void toNewOrder() {
        CartItem cartItem1 = CartItem.create(1L, new ProductBuilder().build(), 1L);
        CartItem cartItem2 = CartItem.create(1L, new ProductBuilder().build(), 1L);

        Cart cart = Cart.of(1L, List.of(cartItem1, cartItem2));

        NewOrder newOrder = cart.toNewOrder();

        assertThat(newOrder.userId()).isEqualTo(1L);
        assertThat(newOrder.items()).hasSize(2);
    }
}