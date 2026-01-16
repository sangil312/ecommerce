package com.dev.core.ecommerce.domain.cart;

import com.dev.core.ecommerce.support.BaseEntity;
import com.dev.core.ecommerce.domain.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "cart_item",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_id_prduct_id", columnNames = {"user_id", "product_id"})
        }
)
public class CartItem extends BaseEntity {
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
    private Long quantity;

    public static CartItem create(Long userId, Product product, Long quantity) {
        CartItem cartItem = new CartItem();
        cartItem.userId = userId;
        cartItem.product = product;
        cartItem.quantity = quantity;
        return cartItem;
    }
}
