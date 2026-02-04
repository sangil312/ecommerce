package com.dev.core.ecommerce.service.order.dto;

import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.enums.order.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderAndItem(
        Long orderId,
        Long userId,
        String orderKey,
        BigDecimal totalPrice,
        OrderStatus status,
        List<OrderItem> items
) {
    public static OrderAndItem of(Order order, List<OrderItem> items) {
        return new OrderAndItem(
                order.getId(),
                order.getUserId(),
                order.getOrderKey(),
                order.getTotalPrice(),
                order.getStatus(),
                items
        );
    }
}