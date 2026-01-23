package com.dev.core.ecommerce.repository.order;

import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
        SELECT item
        FROM OrderItem item
            JOIN Order order ON item.orderId = order.id
        WHERE order.userId = :userId
            AND item.productId = :productId
            AND order.status = :status
            AND item.state = :state
            AND order.createdAt >= :fromDate
    """)
    List<OrderItem> findRecentOrderItemByProduct(
            Long userId,
            Long productId,
            OrderStatus status,
            LocalDateTime fromDate,
            EntityState state
    );
}
