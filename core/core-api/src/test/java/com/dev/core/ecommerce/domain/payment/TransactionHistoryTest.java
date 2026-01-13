package com.dev.core.ecommerce.domain.payment;


import com.dev.core.enums.payment.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionHistoryTest {

    @Test
    void create() {
        LocalDateTime now = LocalDateTime.now();
        TransactionHistory transactionHistory = TransactionHistory.create(
                1L,
                2L,
                3L,
                TransactionType.PAYMENT,
                "external-payment-key",
                BigDecimal.valueOf(1000),
                "결제 성공",
                now
        );

        assertThat(transactionHistory.getUserId()).isEqualTo(1L);
        assertThat(transactionHistory.getOrderId()).isEqualTo(2L);
        assertThat(transactionHistory.getPaymentId()).isEqualTo(3L);
        assertThat(transactionHistory.getType()).isEqualTo(TransactionType.PAYMENT);
        assertThat(transactionHistory.getExternalPaymentKey()).isEqualTo("external-payment-key");
        assertThat(transactionHistory.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(transactionHistory.getMessage()).isEqualTo("결제 성공");
        assertThat(transactionHistory.getApprovedAt()).isEqualTo(now);
    }
}