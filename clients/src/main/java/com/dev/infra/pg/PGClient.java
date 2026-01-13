package com.dev.infra.pg;

import com.dev.infra.pg.dto.ConfirmResult;

import java.math.BigDecimal;

public interface PGClient {
    ConfirmResult requestPaymentConfirm(String paymentKey, String orderKey, BigDecimal amount);
}
