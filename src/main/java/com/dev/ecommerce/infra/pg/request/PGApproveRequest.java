package com.dev.ecommerce.infra.pg.request;

import java.math.BigDecimal;

public record PGApproveRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
}
