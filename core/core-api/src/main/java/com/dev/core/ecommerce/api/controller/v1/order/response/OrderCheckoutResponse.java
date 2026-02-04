package com.dev.core.ecommerce.api.controller.v1.order.response;

import com.dev.core.ecommerce.service.order.dto.OrderAndItem;

import java.math.BigDecimal;
import java.util.List;

public record OrderCheckoutResponse(
        String orderKey,
        BigDecimal totalPrice,
        List<OrderItemResponse> items
) {
    public static OrderCheckoutResponse of(OrderAndItem orderAndItem) {
        return new OrderCheckoutResponse(
                orderAndItem.orderKey(),
                orderAndItem.totalPrice(),
                orderAndItem.items().stream().map(OrderItemResponse::of).toList()
        );
    }
}
