package com.dev.infra.pg.toss.response;

import com.dev.infra.pg.dto.ConfirmResult;
import com.dev.infra.pg.dto.ConfirmSuccess;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TossPaymentsConfirmSuccessResponse(
        String paymentKey,
        String orderId,
        String method,
        String status,
        BigDecimal totalAmount
) {
    public ConfirmResult toPaymentResult() {
        ConfirmSuccess confirmSuccess = new ConfirmSuccess(
                paymentKey,
                orderId,
                method,
                totalAmount,
                "결제 성공",
                LocalDateTime.now()
        );
        return new ConfirmResult(true, null, confirmSuccess);
    }
}
