package com.dev.core.ecommerce.service.order;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.common.error.ApiException;
import com.dev.core.ecommerce.common.error.ErrorType;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrderReader {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Order findOrder(User user, String orderKey, OrderStatus status) {
        Order order = orderRepository.findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE)
                .orElseThrow(() -> new ApiException(ErrorType.ORDER_NOT_FOUND));
        if (!Objects.equals(order.getUserId(), user.id())) throw new ApiException(ErrorType.ORDER_NOT_FOUND);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        if (orderItems.isEmpty()) throw new ApiException(ErrorType.ORDER_NOT_FOUND);

        return order;
    }
}
