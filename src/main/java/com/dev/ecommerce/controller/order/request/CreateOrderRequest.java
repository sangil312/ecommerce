package com.dev.ecommerce.controller.order.request;

import com.dev.ecommerce.domain.User;
import com.dev.ecommerce.domain.order.request.NewOrder;
import com.dev.ecommerce.domain.order.request.NewOrderItem;
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
