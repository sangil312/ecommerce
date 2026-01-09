package com.dev.ecommerce.service.order;

import com.dev.core.ecommerce.service.order.OrderWriter;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import com.dev.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.common.error.ApiException;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.domain.order.request.NewOrder;
import com.dev.core.ecommerce.domain.order.request.NewOrderItem;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.ecommerce.repository.product.ProductRepository;
import com.dev.ecommerce.service.product.ProductBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class OrderWriterTest extends IntegrationTestSupport {
    @Autowired
    private OrderWriter orderWriter;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("단품 주문 생성")
    void create() {
        //given
        Product savedProduct = new ProductBuilder().name("상품1").price(BigDecimal.valueOf(1_000)).build();
        productRepository.save(savedProduct);

        User user = new User(1L);
        NewOrder newOrder = new NewOrder(user.id(), List.of(new NewOrderItem(savedProduct.getId(), 1L)));

        //when
        String orderKey = orderWriter.create(user, newOrder);

        //then
        assertThat(orderKey).isNotBlank();

        Order savedOrder = orderRepository.findByOrderKeyAndState(orderKey, EntityState.ACTIVE).orElseThrow();
        assertThat(savedOrder.getUserId()).isEqualTo(user.id());
        assertThat(savedOrder.getOrderKey()).isEqualTo(orderKey);
        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(1_000L));
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);

        List<OrderItem> savedOrderItems = orderItemRepository.findByOrderId(savedOrder.getId());
        assertThat(savedOrderItems).hasSize(1);
        assertThat(savedOrderItems.getFirst().getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(savedOrderItems.getFirst().getProductId()).isEqualTo(newOrder.items().getFirst().productId());
        assertThat(savedOrderItems.getFirst().getQuantity()).isEqualTo(newOrder.items().getFirst().quantity());
        assertThat(savedOrderItems.getFirst().getProductName()).isEqualTo(savedProduct.getName());
        assertThat(savedOrderItems.getFirst().getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(1_000L));
        assertThat(savedOrderItems.getFirst().getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(1_000L));
    }

    @Test
    @DisplayName("주문 생성 시 상품이 존재하지 않으면 예외가 발생")
    void createWithException() {
        //given
        Long notExistProductId = 99L;
        User user = new User(1L);
        NewOrder newOrder = new NewOrder(user.id(), List.of(new NewOrderItem(notExistProductId, 1L)));

        //when then
        assertThatThrownBy(() -> orderWriter.create(user, newOrder))
                .isInstanceOf(ApiException.class);
    }
}