package com.dev.ecommerce.controller.order.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CreateOrderFromCartRequest(
        @NotEmpty
        Set<Long> cartItemIds
) {
}
