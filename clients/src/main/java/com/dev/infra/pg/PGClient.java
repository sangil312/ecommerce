package com.dev.infra.pg;

import com.dev.infra.pg.dto.ApproveResult;

import java.math.BigDecimal;

public interface PGClient {
    ApproveResult requestPaymentApprove(String paymentKey, String orderKey, BigDecimal amount);
}
