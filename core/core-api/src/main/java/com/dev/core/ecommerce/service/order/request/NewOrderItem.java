package com.dev.core.ecommerce.service.order.request;

public record NewOrderItem(
        Long productId,
        Long quantity
) {
}
