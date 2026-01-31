package com.dev.core.ecommerce.service.order.dto;

import java.util.List;

public record NewOrder(
        Long userId,
        List<NewOrderItem> items
) {
}
