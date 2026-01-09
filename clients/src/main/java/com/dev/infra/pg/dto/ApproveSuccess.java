package com.dev.infra.pg.dto;


import com.dev.core.enums.payment.PaymentMethod;

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
