package com.dev.ecommerce.repository.order;

import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.domain.order.Order;
import com.dev.ecommerce.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderKeyAndState(String orderKey, EntityState state);
    Optional<Order> findByOrderKeyAndStatusAndState(String orderKey, OrderStatus status,  EntityState state);
}
