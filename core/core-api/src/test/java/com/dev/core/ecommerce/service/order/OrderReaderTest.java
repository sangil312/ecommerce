package com.dev.core.ecommerce.service.order;

import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.common.error.ApiException;
import com.dev.core.ecommerce.common.error.ErrorType;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.enums.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class OrderReaderTest extends IntegrationTestSupport {

    @Autowired
    private OrderReader orderReader;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = new User(1L);
        testOrder = orderRepository.save(
                Order.create(testUser.id(), BigDecimal.valueOf(10_000))
        );

        orderItemRepository.save(
                OrderItem.create(
                        testOrder.getId(),
                        1L,
                        1L,
                        "테스트 상품",
                        BigDecimal.valueOf(10_000),
                        BigDecimal.valueOf(10_000)
                )
        );
    }


    @Test
    @DisplayName("주문 조회: 사용자, orderKey, status에 해당하는 Order를 반환")
    void findOrder() {
        // when
        Order result = orderReader.findOrder(testUser, testOrder.getOrderKey(), OrderStatus.CREATED);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testOrder.getId());
        assertThat(result.getUserId()).isEqualTo(testUser.id());
        assertThat(result.getOrderKey()).isEqualTo(testOrder.getOrderKey());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("주문 조회: 존재하지 않는 orderKey가 주어지면 예외 발생")
    void findOrderWithInvalidOrderKeyThrowOrderNotFound() {
        // when then
        assertThatThrownBy(() -> orderReader.findOrder(testUser, "invalid-order-key", OrderStatus.CREATED))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 조회: 요청한 사용자와 조회한 주문의 사용자가 다르면 타입 예외 발생")
    void findOrderWithInvalidUserThrowOrderNotFound() {
        // given
        User differentUser = new User(999L);

        // when then
        assertThatThrownBy(() -> orderReader.findOrder(differentUser, testOrder.getOrderKey(), OrderStatus.CREATED))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 조회: 주문 상태가 맞지 않으면 예외 발생")
    void findOrderWithMismatchStatusThrowOrderNotFound() {
        // when then
        assertThatThrownBy(() -> orderReader.findOrder(testUser, testOrder.getOrderKey(), OrderStatus.PAID))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 조회: 삭제된 주문(EntityState.DELETED)을 조회하면 예외 발생")
    void findOrderWithDeletedEntityThrowOrderNotFound() {
        // given
        testOrder.delete(); // EntityState.DELETED로 변경
        orderRepository.save(testOrder);

        // when then
        assertThatThrownBy(() -> orderReader.findOrder(testUser, testOrder.getOrderKey(), OrderStatus.CREATED))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 조회: 주문에 주문 상품이 없으면 예외 발생")
    void findOrderWithNotFoundOrderItemThrowOrderNotFound() {
        // given
        orderItemRepository.deleteAll();

        // when then
        assertThatThrownBy(() -> orderReader.findOrder(testUser, testOrder.getOrderKey(), OrderStatus.CREATED))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND);
    }
}