package com.dev.core.ecommerce.service.order;

import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.ecommerce.service.order.dto.OrderAndItem;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.enums.EntityState;
import com.dev.core.enums.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderReaderUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderReader orderReader;

    private final Long userId = 1L;

    @Test
    @DisplayName("주문서 조회: 주문과 주문 상품을 함께 응답")
    void findOrderAndItems() {
        // given
        Order order = Order.create(userId, BigDecimal.valueOf(10_000));
        OrderStatus status = OrderStatus.CREATED;
        String orderKey = order.getOrderKey();
        List<OrderItem> items = List.of(
                OrderItem.create(
                        1L,
                        10L,
                        2L,
                        "상품1",
                        "thumbnail-url",
                        BigDecimal.valueOf(5_000),
                        BigDecimal.valueOf(10_000)
                )
        );

        when(orderRepository.findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE))
                .thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(order.getId())).thenReturn(items);

        // when
        OrderAndItem result = orderReader.findOrderAndItems(userId, orderKey, status);

        // then
        assertThat(result.orderId()).isEqualTo(order.getId());
        assertThat(result.userId()).isEqualTo(order.getUserId());
        assertThat(result.orderKey()).isEqualTo(order.getOrderKey());
        assertThat(result.totalPrice()).isEqualTo(order.getTotalPrice());
        assertThat(result.status()).isEqualTo(order.getStatus());
        assertThat(result.items()).containsExactlyElementsOf(items);

        verify(orderRepository).findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE);
        verify(orderItemRepository).findByOrderId(order.getId());
    }

    @Test
    @DisplayName("주문서 조회: 주문이 없으면 예외 발생")
    void findOrderAndItemsWithInvalidOrderThrowException() {
        // given
        OrderStatus status = OrderStatus.CREATED;
        String orderKey = "invalid-order-key";

        when(orderRepository.findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE))
                .thenReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> orderReader.findOrderAndItems(userId, orderKey, status))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND);

        verify(orderRepository).findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE);
    }

    @Test
    @DisplayName("주문서 조회: 주문 상품이 없으면 예외 발생")
    void findOrderAndItemsWithEmptyItemsThrowException() {
        // given
        Order order = Order.create(userId, BigDecimal.valueOf(10_000));
        OrderStatus status = OrderStatus.CREATED;
        String orderKey = order.getOrderKey();

        when(orderRepository.findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE))
                .thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(order.getId()))
                .thenReturn(List.of());

        // when then
        assertThatThrownBy(() -> orderReader.findOrderAndItems(userId, orderKey, status))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND);

        verify(orderRepository).findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE);
        verify(orderItemRepository).findByOrderId(order.getId());
    }

    @Test
    @DisplayName("주문서 조회: 사용자 불일치면 예외 발생")
    void findOrderAndItemsWithUserMismatchThrowException() {
        // given
        Order diffrentOrder = Order.create(2L, BigDecimal.valueOf(10_000));
        OrderStatus status = OrderStatus.CREATED;
        String orderKey = diffrentOrder.getOrderKey();

        when(orderRepository.findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE))
                .thenReturn(Optional.of(diffrentOrder));

        // when then
        assertThatThrownBy(() -> orderReader.findOrderAndItems(userId, orderKey, status))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND);

        verify(orderRepository).findByOrderKeyAndStatusAndState(orderKey, status, EntityState.ACTIVE);
    }
}
