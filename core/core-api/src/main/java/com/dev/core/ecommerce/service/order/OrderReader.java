package com.dev.core.ecommerce.service.order;

import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.ecommerce.service.order.dto.OrderAndItem;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
@RequiredArgsConstructor
public class OrderReader {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Order findOrder(Long userId, String orderKey, OrderStatus status) {
        var order = orderRepository.findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE)
                .orElseThrow(() -> new ApiException(ErrorType.ORDER_NOT_FOUND));
        if (!Objects.equals(order.getUserId(), userId)) throw new ApiException(ErrorType.ORDER_NOT_FOUND);

        var existsOrderItem = orderItemRepository.existsByOrderId(order.getId());
        if (!existsOrderItem) throw new ApiException(ErrorType.ORDER_NOT_FOUND);

        return order;
    }

    public OrderAndItem findOrderAndItems(Long userId, String orderKey, OrderStatus status) {
        var order = orderRepository.findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE)
                .orElseThrow(() -> new ApiException(ErrorType.ORDER_NOT_FOUND));
        if (!Objects.equals(order.getUserId(), userId)) throw new ApiException(ErrorType.ORDER_NOT_FOUND);

        var orderItems = orderItemRepository.findByOrderId(order.getId());
        if (orderItems.isEmpty()) throw new ApiException(ErrorType.ORDER_NOT_FOUND);

        return OrderAndItem.of(order, orderItems);
    }
}
