package com.dev.core.ecommerce.api.controller.v1.order.request;

import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.service.order.dto.NewOrder;
import com.dev.core.ecommerce.service.order.dto.NewOrderItem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest(
        @Positive
        @NotNull
        Long productId,

        @Positive
        @NotNull
        Long quantity
) {
    public NewOrder toNewOrder(User user) {
        return new NewOrder(user.id(), List.of(new NewOrderItem(productId, quantity)));
    }
}
