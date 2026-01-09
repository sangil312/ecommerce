package com.dev.core.ecommerce.domain.order.request;

public record NewOrderItem(
        Long productId,
        Long quantity
) {
}
