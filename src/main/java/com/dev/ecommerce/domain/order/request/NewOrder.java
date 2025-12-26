package com.dev.ecommerce.domain.order.request;

import java.util.List;

public record NewOrder(
        Long userId,
        List<NewOrderItem> items
) {
}
