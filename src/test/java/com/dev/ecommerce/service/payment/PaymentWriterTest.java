package com.dev.ecommerce.service.payment;

import com.dev.ecommerce.IntegrationTestSupport;
import com.dev.ecommerce.common.error.ApiException;
import com.dev.ecommerce.domain.order.Order;
import com.dev.ecommerce.domain.payment.Payment;
import com.dev.ecommerce.domain.payment.PaymentStatus;
import com.dev.ecommerce.repository.PaymentRepository;
import com.dev.ecommerce.repository.order.OrderRepository;
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

    @Test
    @DisplayName("결제 생성")
    void create() {
        //given
        Long userId = 1L;
        Order savedOrder = orderRepository.save(Order.create(userId, BigDecimal.valueOf(1000)));

        //when
        Long paymentId = paymentWriter.create(savedOrder);

        //then
        Payment createdPayment = paymentRepository.findById(paymentId).orElseThrow();
        assertThat(createdPayment.getId()).isEqualTo(paymentId);
        assertThat(createdPayment.getUserId()).isEqualTo(savedOrder.getUserId());
        assertThat(createdPayment.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(createdPayment.getAmount()).isEqualTo(savedOrder.getTotalPrice());
        assertThat(createdPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(createdPayment.getExternalPaymentKey()).isNull();
        assertThat(createdPayment.getApproveCode()).isNull();
        assertThat(createdPayment.getPaidAt()).isNull();
    }

    @Test
    @DisplayName("결제 생성 시 결제 성공 상태인 결제일 경우 예외 발생")
    void createFail() {
        Long userId = 1L;
        Order savedOrder = orderRepository.save(Order.create(userId, BigDecimal.valueOf(1000)));

        Long paymentId = paymentWriter.create(savedOrder);

        Payment createdPayment = paymentRepository.findById(paymentId).orElseThrow();
        createdPayment.success(null, null);

        assertThatThrownBy(() -> paymentWriter.create(savedOrder))
                .isInstanceOf(ApiException.class);

    }
}