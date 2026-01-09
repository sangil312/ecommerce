package com.dev.ecommerce.domain.payment;

import com.dev.ecommerce.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment extends BaseEntity {
    private Long userId;
    private Long orderId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String externalPaymentKey;
    private PaymentMethod method;
    private LocalDateTime paidAt;

    public static Payment create(
            Long userId,
            Long orderId,
            BigDecimal amount
    ) {
        Payment payment = new Payment();
        payment.userId = userId;
        payment.orderId = orderId;
        payment.amount = amount;
        payment.status = PaymentStatus.READY;
        return payment;
    }

    public void success(String externalPaymentKey, PaymentMethod method) {
        this.status = PaymentStatus.SUCCESS;
        this.externalPaymentKey = externalPaymentKey;
        this.method = method;
        this.paidAt = LocalDateTime.now();
    }

    public void pending(String externalPaymentKey, PaymentMethod method) {
        this.status = PaymentStatus.PENDING;
        this.externalPaymentKey = externalPaymentKey;
        this.method = method;
    }
}
