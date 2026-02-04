package com.dev.core.ecommerce.service.order;

import com.dev.core.ecommerce.service.order.dto.OrderAndItem;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.service.order.dto.NewOrder;
import com.dev.core.enums.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderWriter orderWriter;
    private final OrderReader orderReader;

    public String create(User user, NewOrder newOrder) {
        return orderWriter.createOrder(user, newOrder);
    }

    public OrderAndItem findOrderAndItems(User user, String orderKey, OrderStatus status) {
        return orderReader.findOrderAndItems(user, orderKey, status);
    }
}
