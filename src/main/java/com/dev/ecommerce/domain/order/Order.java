package com.dev.ecommerce.domain.order;

import com.dev.ecommerce.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {
    private Long userId;
    private String orderKey;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public static Order create(Long userId, BigDecimal totalPrice) {
        Order order = new Order();
        order.userId = userId;
        order.orderKey = UUID.randomUUID().toString();
        order.totalPrice = totalPrice;
        order.status = OrderStatus.CREATED;
        return order;
    }
}
