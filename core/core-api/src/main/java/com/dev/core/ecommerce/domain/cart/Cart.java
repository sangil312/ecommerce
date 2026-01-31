package com.dev.core.ecommerce.domain.cart;

import com.dev.core.ecommerce.service.order.dto.NewOrder;
import com.dev.core.ecommerce.service.order.dto.NewOrderItem;

import java.util.List;

public record Cart(
    Long userId,
    List<CartItem> items
) {
    public static Cart of(Long userId, List<CartItem> items) {
        return new Cart(userId, items);
    }

    public NewOrder toNewOrder() {
        List<NewOrderItem> newOrderItems = items.stream()
                .map(it -> new NewOrderItem(it.getProduct().getId(), it.getQuantity()))
                .toList();

        return new NewOrder(userId, newOrderItems);
    }
}
