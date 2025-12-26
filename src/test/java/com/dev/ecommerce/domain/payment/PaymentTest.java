package com.dev.ecommerce.domain.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


class PaymentTest {

    @Test
    void create() {
        Payment payment = Payment.create(1L, 1L, BigDecimal.valueOf(1000));

        assertThat(payment.getUserId()).isEqualTo(1L);
        assertThat(payment.getOrderId()).isEqualTo(1L);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(payment.getExternalPaymentKey()).isNull();
        assertThat(payment.getApproveCode()).isNull();
        assertThat(payment.getPaidAt()).isNull();
    }
}