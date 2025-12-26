package com.dev.ecommerce.domain.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


class OrderItemTest {

    @Test
    void crate() {
        BigDecimal unitPrice = BigDecimal.valueOf(1_000L);
        BigDecimal totalPrice = BigDecimal.valueOf(1_000L);
        OrderItem orderItem = OrderItem.create(1L, 1L, 1L, "상품1", unitPrice, totalPrice);

        assertThat(orderItem.getOrderId()).isEqualTo(1L);
        assertThat(orderItem.getProductId()).isEqualTo(1L);
        assertThat(orderItem.getProductName()).isEqualTo("상품1");
        assertThat(orderItem.getQuantity()).isEqualTo(1L);
        assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderItem.getTotalPrice()).isEqualTo(totalPrice);
    }
}