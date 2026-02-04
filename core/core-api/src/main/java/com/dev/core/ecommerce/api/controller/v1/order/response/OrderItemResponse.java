package com.dev.core.ecommerce.api.controller.v1.order.response;

import com.dev.core.ecommerce.domain.order.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        Long quantity,
        String thumbnailUrl,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
    public static OrderItemResponse of(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProductId(),
                orderItem.getProductName(),
                orderItem.getQuantity(),
                orderItem.getThumbnailUrl(),
                orderItem.getUnitPrice(),
                orderItem.getTotalPrice()
        );
    }
}