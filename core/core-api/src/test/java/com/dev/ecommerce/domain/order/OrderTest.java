package com.dev.ecommerce.domain.order;

import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


class OrderTest {

    @Test
    void create() {
        BigDecimal totalPrice = BigDecimal.valueOf(1_000L);
        Order order = Order.create(1L, totalPrice);

        assertThat(order.getUserId()).isNotNull();
        assertThat(order.getOrderKey()).isNotNull();
        assertThat(order.getTotalPrice()).isEqualByComparingTo(totalPrice);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getState()).isEqualTo(EntityState.ACTIVE);
    }
}