package com.dev.core.ecommerce.service.payment.dto;

import com.dev.core.enums.payment.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentConfirmSuccess(
        String externalPaymentKey,
        String orderKey,
        PaymentMethod method,
        BigDecimal amount,
        LocalDateTime approvedAt
) {
}
