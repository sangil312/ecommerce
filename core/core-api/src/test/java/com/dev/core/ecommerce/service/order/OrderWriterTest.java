package com.dev.core.ecommerce.service.order;

import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.service.order.dto.NewOrder;
import com.dev.core.ecommerce.service.order.dto.NewOrderItem;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.ecommerce.repository.product.ProductRepository;
import com.dev.core.ecommerce.service.product.ProductBuilder;
import org.junit.jupiter.api.BeforeEach;
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

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User(1L);
        testProduct = new ProductBuilder().name("상품1").price(BigDecimal.valueOf(1_000)).build();
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @DisplayName("주문 생성")
    void createOrder() {
        //given
        NewOrder newOrder = new NewOrder(testUser.id(), List.of(new NewOrderItem(testProduct.getId(), 1L)));

        //when
        String orderKey = orderWriter.createOrder(testUser, newOrder);

        //then
        assertThat(orderKey).isNotBlank();

        Order savedOrder = orderRepository.findByOrderKeyAndState(orderKey, EntityState.ACTIVE).orElseThrow();
        assertThat(savedOrder.getUserId()).isEqualTo(testUser.id());
        assertThat(savedOrder.getOrderKey()).isEqualTo(orderKey);
        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(1_000L));
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);

        List<OrderItem> savedOrderItems = orderItemRepository.findByOrderId(savedOrder.getId());
        assertThat(savedOrderItems).hasSize(2);
        assertThat(savedOrderItems.getFirst().getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(savedOrderItems.getFirst().getProductId()).isEqualTo(newOrder.items().getFirst().productId());
        assertThat(savedOrderItems.getFirst().getQuantity()).isEqualTo(newOrder.items().getFirst().quantity());
        assertThat(savedOrderItems.getFirst().getProductName()).isEqualTo(testProduct.getName());
        assertThat(savedOrderItems.getFirst().getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(1_000L));
        assertThat(savedOrderItems.getFirst().getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(1_000L));
    }

    @Test
    @DisplayName("주문 생성: 상품이 존재하지 않으면 예외 발생")
    void createOrderWithProductNotFound() {
        //given
        Long notExistProductId = 99L;
        User user = new User(1L);
        NewOrder newOrder = new NewOrder(user.id(), List.of(new NewOrderItem(notExistProductId, 1L)));

        //when then
        assertThatThrownBy(() -> orderWriter.createOrder(user, newOrder))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 생성: 주문 요청 상품 중 존재하지 않는 상품이 있을 경우 타입 예외 발생")
    void createOrderWithProductMismatch() {
        //given
        User user = new User(1L);
        NewOrderItem newOrderItem1 = new NewOrderItem(testProduct.getId(), 1L);
        NewOrderItem newOrderItem2 = new NewOrderItem(999L, 1L);
        NewOrder newOrder = new NewOrder(user.id(), List.of(newOrderItem1, newOrderItem2));

        //when then
        assertThatThrownBy(() -> orderWriter.createOrder(user, newOrder))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PRODUCT_MISMATCH_IN_ORDER);
    }
}