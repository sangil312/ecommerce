package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.enums.payment.PaymentMethod;
import com.dev.core.enums.payment.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class PaymentProcessorTest extends IntegrationTestSupport {
    @Autowired
    private PaymentProcessor paymentProcessor;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private User testUser;
    private Order testOrder;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testUser = new User(1L);
        testOrder = orderRepository.save(
                Order.create(testUser.id(), BigDecimal.valueOf(10_000))
        );
        testPayment = paymentRepository.save(
                Payment.create(testUser.id(), testOrder.getId(), BigDecimal.valueOf(10_000))
        );

        orderItemRepository.save(
                OrderItem.create(
                        testOrder.getId(),
                        1L,
                        1L,
                        "테스트 상품",
                        "thumbnail-url",
                        BigDecimal.valueOf(10_000),
                        BigDecimal.valueOf(10_000)
                )
        );
    }

    @Test
    @DisplayName("결제 요청 검증 성공")
    void validatePayment() {
        // when
        Payment validatedPayment = paymentProcessor.validatePayment(
                testUser,
                testOrder.getOrderKey(),
                BigDecimal.valueOf(10_000)
        );

        // then
        assertThat(validatedPayment).isNotNull();
        assertThat(validatedPayment.getId()).isEqualTo(testPayment.getId());
        assertThat(validatedPayment.getUserId()).isEqualTo(testUser.id());
        assertThat(validatedPayment.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(validatedPayment.getStatus()).isEqualTo(PaymentStatus.READY);
    }

    @Test
    @DisplayName("결제 요청 검증: 유효하지않은 주문일 경우 예외 발생")
    void validatePaymentWithOrderNotFound() {
        // given
        String invalidOrderKey = "invalid-order-key";
        // when then
        assertThatThrownBy(() ->
                paymentProcessor.validatePayment(testUser, invalidOrderKey, BigDecimal.valueOf(10_000))
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("결제 요청 검증: 결제를 주문ID로 조회 시 존재하지 않으면 예외 발생")
    void validatePaymentWithPaymentNotFound() {
        // given
        Order newOrder = orderRepository.save(Order.create(testUser.id(), BigDecimal.valueOf(10_000)));

        orderItemRepository.save(
                OrderItem.create(
                        newOrder.getId(),
                        1L,
                        1L,
                        "테스트 상품",
                        "thumbnail-url",
                        BigDecimal.valueOf(10_000),
                        BigDecimal.valueOf(10_000)
                )
        );
        
        // when then
        assertThatThrownBy(() ->
                paymentProcessor.validatePayment(testUser, newOrder.getOrderKey(), BigDecimal.valueOf(10_000))
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("결제 요청 검증: 결제 금액이 일치하지 않을 경우 예외 발생")
    void validatePaymentWithAmountMismatch() {
        // when then - 다른 금액으로 검증 시도
        assertThatThrownBy(() ->
                paymentProcessor.validatePayment(testUser, testOrder.getOrderKey(), BigDecimal.valueOf(5_000))
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_AMOUNT_MISMATCH);
    }

    @Test
    @DisplayName("결제 요청 검증: 결제 상태가 READY가 아닐 경우 예외 발생")
    void validatePaymentWithAlreadyPaid() {
        // given
        Payment payment = paymentRepository.findById(testPayment.getId()).orElseThrow();
        payment.success("ext_key_123", PaymentMethod.CARD);

        // when then
        assertThatThrownBy(() ->
                paymentProcessor.validatePayment(testUser, testOrder.getOrderKey(), BigDecimal.valueOf(10_000))
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_ALREADY_PAID);
    }

    @Test
    @DisplayName("결제 요청 검증: 결제 상태가 READY가 아닐 경우 예외 발생")
    void validatePaymentWithNotEqualsUser() {
        // given
        User user = new User(99L);
        Order newOrder = orderRepository.save(Order.create(user.id(), BigDecimal.valueOf(10_000)));

        orderItemRepository.save(
                OrderItem.create(newOrder.getId(),
                        1L,
                        1L,
                        "테스트 상품",
                        "thumbnail-url",
                        BigDecimal.valueOf(10_000),
                        BigDecimal.valueOf(10_000)
                )
        );

        // when then
        assertThatThrownBy(() ->
                paymentProcessor.validatePayment(user, newOrder.getOrderKey(), BigDecimal.valueOf(10_000))
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);
    }
}
