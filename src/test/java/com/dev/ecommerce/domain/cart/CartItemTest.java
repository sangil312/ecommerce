package com.dev.ecommerce.domain.cart;

import com.dev.ecommerce.domain.product.Product;
import com.dev.ecommerce.service.product.ProductBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemTest {

    @Test
    void create() {
        Product product = new ProductBuilder().build();
        CartItem cartItem = CartItem.create(1L, product, 1L);

        assertThat(cartItem).isNotNull();
        assertThat(cartItem.getProduct()).isEqualTo(product);
        assertThat(cartItem.getQuantity()).isEqualTo(1);
    }
}