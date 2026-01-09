package com.dev.core.ecommerce.domain.order;

import com.dev.core.ecommerce.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_item")
public class OrderItem extends BaseEntity {
    private Long orderId;
    private Long productId;
    private Long quantity;
    private String productName;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public static OrderItem create(
            Long orderId,
            Long productId,
            Long quantity,
            String productName,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
        OrderItem orderItem = new OrderItem();
        orderItem.orderId = orderId;
        orderItem.productId = productId;
        orderItem.quantity = quantity;
        orderItem.productName = productName;
        orderItem.unitPrice = unitPrice;
        orderItem.totalPrice = totalPrice;
        return orderItem;
    }
}
