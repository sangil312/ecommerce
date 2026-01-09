package com.dev.infra.pg;

import com.dev.infra.pg.dto.ApproveClientResult;

import java.math.BigDecimal;

public interface PGClient {
    ApproveClientResult approvePayment(String paymentKey, String orderKey, BigDecimal amount);
}
