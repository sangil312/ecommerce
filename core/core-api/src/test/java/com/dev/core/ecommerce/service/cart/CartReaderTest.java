package com.dev.core.ecommerce.service.cart;

import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.domain.cart.Cart;
import com.dev.core.ecommerce.domain.cart.CartItem;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.repository.cart.CartItemRepository;
import com.dev.core.ecommerce.repository.product.ProductRepository;
import com.dev.core.ecommerce.service.product.ProductBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class CartReaderTest extends IntegrationTestSupport {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartReader cartReader;

    @Test
    @DisplayName("장바구니 조회")
    void findCart() {
        //given
        User user = new User(1L);
        Product product1 = new ProductBuilder().name("상품1").price(BigDecimal.valueOf(1_000)).build();
        Product product2 = new ProductBuilder().name("상품2").price(BigDecimal.valueOf(2_000)).build();
        productRepository.saveAll(List.of(product1, product2));

        CartItem cartItem1 = CartItem.create(user.id(), product1, 1L);
        CartItem cartItem2 = CartItem.create(user.id(), product2, 2L);
        cartItemRepository.saveAll(List.of(cartItem1, cartItem2));

        //when
        Cart cart = cartReader.findCart(user, List.of(cartItem1.getId(), cartItem2.getId()));

        //then
        assertThat(cart.userId()).isEqualTo(user.id());
        assertThat(cart.items()).hasSize(2)
                .extracting(CartItem::getUserId, CartItem::getProduct,  CartItem::getQuantity)
                .containsExactlyInAnyOrder(
                        tuple(user.id(), product1, cartItem1.getQuantity()),
                        tuple(user.id(), product2, cartItem2.getQuantity())
                );

    }
}