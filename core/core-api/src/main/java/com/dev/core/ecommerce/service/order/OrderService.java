package com.dev.core.ecommerce.service.order;

import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.service.order.dto.NewOrder;
import com.dev.core.ecommerce.service.cart.CartReader;
import com.dev.core.enums.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderWriter orderWriter;
    private final OrderReader orderReader;
    private final CartReader cartReader;

    public String createOrder(User user, NewOrder newOrder) {
        return orderWriter.createOrder(user, newOrder);
    }

    public Order findOrder(User user, String orderKey, OrderStatus status) {
        return orderReader.findOrder(user, orderKey, status);
    }

    public String createFromCart(User user, Collection<Long> cartItemIds) {
        var cart = cartReader.findCart(user, cartItemIds);
        return createOrder(user, cart.toNewOrder());
    }
}
