package com.dev.core.ecommerce.domain.payment;

import com.dev.core.ecommerce.common.BaseEntity;
import com.dev.core.enums.payment.TransactionType;
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
        name = "transaction_history",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_transaction_history_order_id", columnNames = "order_id")
        }
)
public class TransactionHistory extends BaseEntity {
    private Long userId;
    private Long orderId;
    private Long paymentId;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private String externalPaymentKey;
    private BigDecimal amount;
    private String message;
    private LocalDateTime occurredAt;

    public static TransactionHistory create(
            Long userId,
            Long orderId,
            Long paymentId,
            TransactionType type,
            String externalPaymentKey,
            BigDecimal amount,
            String message,
            LocalDateTime approvedAt
    ) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.userId = userId;
        transactionHistory.orderId = orderId;
        transactionHistory.paymentId = paymentId;
        transactionHistory.type = type;
        transactionHistory.externalPaymentKey = externalPaymentKey;
        transactionHistory.amount = amount;
        transactionHistory.message = message;
        transactionHistory.occurredAt = approvedAt;
        return transactionHistory;
    }
}
