package com.dev.ecommerce.service.payment.response;

import com.dev.ecommerce.domain.payment.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApproveSuccess(
        String externalPaymentKey,
        String orderKey,
        PaymentMethod method,
        BigDecimal amount,
        String message,
        LocalDateTime approvedAt
) {
}
