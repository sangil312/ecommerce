package com.dev.ecommerce.service.order;

import com.dev.ecommerce.IntegrationTestSupport;
import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.common.error.ApiException;
import com.dev.ecommerce.domain.User;
import com.dev.ecommerce.domain.order.Order;
import com.dev.ecommerce.domain.order.OrderItem;
import com.dev.ecommerce.domain.order.OrderStatus;
import com.dev.ecommerce.domain.order.request.NewOrder;
import com.dev.ecommerce.domain.order.request.NewOrderItem;
import com.dev.ecommerce.domain.product.Product;
import com.dev.ecommerce.repository.order.OrderItemRepository;
import com.dev.ecommerce.repository.order.OrderRepository;
import com.dev.ecommerce.repository.product.ProductRepository;
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
    @DisplayName("주문을 생성한다.")
    void create() {
        //given
        Product product = Product.create(
                "상품1",
                "https://~",
                "설명",
                "짧은 설명",
                BigDecimal.valueOf(1_000L)
        );
        Product savedProduct = productRepository.save(product);

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
    @DisplayName("주문 생성 시 상품이 존재하지 않으면 예외가 발생한다.")
    void createWithException() {
        //given
        Product product = Product.create(
                "상품1",
                "https://~",
                "설명",
                "짧은 설명",
                BigDecimal.valueOf(1_000L)
        );
        productRepository.save(product);

        Long notExistProductId = 99L;
        User user = new User(1L);
        NewOrder newOrder = new NewOrder(user.id(), List.of(new NewOrderItem(notExistProductId, 1L)));

        //when then
        assertThatThrownBy(() -> orderWriter.create(user, newOrder))
                .isInstanceOf(ApiException.class);
    }
}