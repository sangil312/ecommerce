package com.dev.infra.pg.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApproveSuccess(
        String externalPaymentKey,
        String orderKey,
        String method,
        BigDecimal amount,
        String message,
        LocalDateTime approvedAt
) {
}
