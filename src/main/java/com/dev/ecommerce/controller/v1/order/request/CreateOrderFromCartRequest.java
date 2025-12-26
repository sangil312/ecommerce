package com.dev.ecommerce.controller.v1.order.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CreateOrderFromCartRequest(
        @NotEmpty
        Set<Long> cartItemIds
) {
}
