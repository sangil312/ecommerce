package com.dev.core.ecommerce.service.order.dto;

public record NewOrderItem(
        Long productId,
        Long quantity
) {
}
