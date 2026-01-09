package com.dev.core.ecommerce.repository.order;

import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderKeyAndState(String orderKey, EntityState state);
    Optional<Order> findByOrderKeyAndStatusAndState(String orderKey, OrderStatus status, EntityState state);
}
