package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.common.error.ErrorType;
import com.dev.core.enums.payment.PaymentMethod;
import com.dev.core.enums.payment.PaymentStatus;
import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.common.error.ApiException;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Transactional
class PaymentWriterTest extends IntegrationTestSupport {
    @Autowired
    private PaymentWriter paymentWriter;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        User testUser = new User(1L);
        testOrder = orderRepository.save(Order.create(testUser.id(), BigDecimal.valueOf(1000)));
    }

    @Test
    @DisplayName("결제 생성")
    void create() {
        //when
        Long paymentId = paymentWriter.create(testOrder);

        //then
        Payment createdPayment = paymentRepository.findById(paymentId).orElseThrow();
        assertThat(createdPayment.getId()).isEqualTo(paymentId);
        assertThat(createdPayment.getUserId()).isEqualTo(testOrder.getUserId());
        assertThat(createdPayment.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(createdPayment.getAmount()).isEqualTo(testOrder.getTotalPrice());
        assertThat(createdPayment.getStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(createdPayment.getExternalPaymentKey()).isNull();
        assertThat(createdPayment.getMethod()).isNull();
        assertThat(createdPayment.getPaidAt()).isNull();
    }

    @Test
    @DisplayName("결제 생성: 결제 성공 상태(PaymentStatus.SUCCESS)인 결제일 경우 예외 발생")
    void createWithAlreadyPaid() {
        //when
        Long paymentId = paymentWriter.create(testOrder);

        Payment createdPayment = paymentRepository.findById(paymentId).orElseThrow();
        createdPayment.success(null, PaymentMethod.CARD);

        assertThatThrownBy(() -> paymentWriter.create(testOrder))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_ALREADY_PAID);
    }
}