package com.dev.infra.pg.toss.request;

import java.math.BigDecimal;

public record ConfirmRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
}
