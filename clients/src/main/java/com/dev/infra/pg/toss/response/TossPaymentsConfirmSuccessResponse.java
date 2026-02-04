package com.dev.infra.pg.toss.response;

import com.dev.infra.pg.dto.ApproveResult;
import com.dev.infra.pg.dto.ApproveSuccess;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TossPaymentsConfirmSuccessResponse(
        String paymentKey,
        String orderId,
        String method,
        String status,
        BigDecimal totalAmount
) {
    public ApproveResult toPaymentResult() {
        ApproveSuccess approveSuccess = new ApproveSuccess(
                paymentKey,
                orderId,
                method,
                totalAmount,
                "결제 성공",
                LocalDateTime.now()
        );
        return new ApproveResult(true, null, approveSuccess);
    }
}
