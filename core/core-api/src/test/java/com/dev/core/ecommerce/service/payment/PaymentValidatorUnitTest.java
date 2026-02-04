package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.ecommerce.service.order.OrderReader;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.enums.payment.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentValidatorUnitTest {

    @Mock
    private OrderReader orderReader;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentValidator paymentValidator;

    private final Long userId = 1L;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        order = Order.create(userId, BigDecimal.valueOf(10_000));
        payment = Payment.create(userId, order.getId(), BigDecimal.valueOf(10_000));
    }

    @Test
    @DisplayName("결제 검증: 정상 결제 정보면 결제 엔티티 반환")
    void validatePayment() {
        when(orderReader.findOrder(userId, order.getOrderKey(), OrderStatus.CREATED)).thenReturn(order);
        when(paymentRepository.findByOrderId(order.getId())).thenReturn(Optional.of(payment));

        // when
        Payment result = paymentValidator.validatePayment(userId, order.getOrderKey(), BigDecimal.valueOf(10_000));

        // then
        assertThat(result).isEqualTo(payment);

        verify(orderReader).findOrder(userId, order.getOrderKey(), OrderStatus.CREATED);
        verify(paymentRepository).findByOrderId(order.getId());
    }

    @Test
    @DisplayName("결제 검증: 결제 정보가 없으면 예외 발생")
    void validatePaymentWithPaymentNotFound() {
        when(orderReader.findOrder(userId, order.getOrderKey(), OrderStatus.CREATED)).thenReturn(order);
        when(paymentRepository.findByOrderId(order.getId())).thenReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> paymentValidator.validatePayment(userId, order.getOrderKey(), BigDecimal.valueOf(10_000)))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);

        verify(orderReader).findOrder(userId, order.getOrderKey(), OrderStatus.CREATED);
        verify(paymentRepository).findByOrderId(order.getId());
    }

    @Test
    @DisplayName("결제 검증: 결제 사용자 불일치면 예외 발생")
    void validatePaymentWithUserMismatch() {
        Payment diffrentPayment = Payment.create(2L, order.getId(), BigDecimal.valueOf(10_000));

        when(orderReader.findOrder(userId, order.getOrderKey(), OrderStatus.CREATED)).thenReturn(order);
        when(paymentRepository.findByOrderId(order.getId())).thenReturn(Optional.of(diffrentPayment));

        // when then
        assertThatThrownBy(() -> paymentValidator.validatePayment(userId, order.getOrderKey(), BigDecimal.valueOf(10_000)))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);

        verify(orderReader).findOrder(userId, order.getOrderKey(), OrderStatus.CREATED);
        verify(paymentRepository).findByOrderId(order.getId());
    }

    @Test
    @DisplayName("결제 검증: 결제 상태가 READY가 아니면 예외 발생")
    void validatePaymentWithAlreadyPaid() {
        payment.success("ext_key", PaymentMethod.CARD);

        when(orderReader.findOrder(userId, order.getOrderKey(), OrderStatus.CREATED)).thenReturn(order);
        when(paymentRepository.findByOrderId(order.getId())).thenReturn(Optional.of(payment));

        // when then
        assertThatThrownBy(() -> paymentValidator.validatePayment(userId, order.getOrderKey(), BigDecimal.valueOf(10_000)))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_ALREADY_PAID);

        verify(orderReader).findOrder(userId, order.getOrderKey(), OrderStatus.CREATED);
        verify(paymentRepository).findByOrderId(order.getId());
    }

    @Test
    @DisplayName("결제 검증: 결제 금액 불일치면 예외 발생")
    void validatePaymentWithAmountMismatch() {
        when(orderReader.findOrder(userId, order.getOrderKey(), OrderStatus.CREATED)).thenReturn(order);
        when(paymentRepository.findByOrderId(order.getId())).thenReturn(Optional.of(payment));

        // when then
        assertThatThrownBy(() -> paymentValidator.validatePayment(userId, order.getOrderKey(), BigDecimal.valueOf(5_000)))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_AMOUNT_MISMATCH);

        verify(orderReader).findOrder(userId, order.getOrderKey(), OrderStatus.CREATED);
        verify(paymentRepository).findByOrderId(order.getId());
    }
}
