package com.dev.ecommerce.domain.cart;

import com.dev.ecommerce.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "cart_item")
public class CartItem extends BaseEntity {
    private Long userId;
    private Long productId;
    private Long quantity;
}
