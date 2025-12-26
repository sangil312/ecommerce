package com.dev.ecommerce.domain.order.request;

public record NewOrderItem(
        Long productId,
        Long quantity
) {
}
