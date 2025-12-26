package com.dev.ecommerce.service.order;

import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.common.error.ApiException;
import com.dev.ecommerce.common.error.ErrorType;
import com.dev.ecommerce.domain.order.Order;
import com.dev.ecommerce.domain.order.OrderItem;
import com.dev.ecommerce.domain.order.OrderStatus;
import com.dev.ecommerce.repository.order.OrderItemRepository;
import com.dev.ecommerce.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrderReader {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Order find(User user, String orderKey, OrderStatus status) {
        Order order = orderRepository.findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE)
                .orElseThrow(() -> new ApiException(ErrorType.ORDER_NOT_FOUND));
        if (!Objects.equals(order.getUserId(), user.id())) throw new ApiException(ErrorType.ORDER_NOT_FOUND);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        if (orderItems.isEmpty()) throw new ApiException(ErrorType.ORDER_NOT_FOUND);

        return order;
    }
}
