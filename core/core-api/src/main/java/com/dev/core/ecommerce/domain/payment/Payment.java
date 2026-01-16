package com.dev.core.ecommerce.domain.payment;

import com.dev.core.ecommerce.common.BaseEntity;
import com.dev.core.enums.payment.PaymentMethod;
import com.dev.core.enums.payment.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "payment",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_id", columnNames = "order_id")
        }
)
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

    public void pending(String externalPaymentKey) {
        this.status = PaymentStatus.PENDING;
        this.externalPaymentKey = externalPaymentKey;
    }

    public void error(String externalPaymentKey, PaymentMethod method) {
        this.status = PaymentStatus.ERROR;
        this.externalPaymentKey = externalPaymentKey;
        this.method = method;
    }

    public void fail() {
        this.status = PaymentStatus.FAIL;
    }
}
