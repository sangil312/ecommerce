package com.dev.infra.pg.toss.response;

import com.dev.core.enums.payment.PaymentMethod;
import com.dev.infra.pg.dto.ApproveClientResult;
import com.dev.infra.pg.dto.ApproveSuccess;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TossPaymentsApproveSuccessResponse(
        String paymentKey,
        String orderId,
        String method,
        String status,
        BigDecimal totalAmount
) {
    public ApproveClientResult toPaymentResult() {
        ApproveSuccess approveSuccess = new ApproveSuccess(
                paymentKey,
                orderId,
                PaymentMethod.fromValue(method),
                totalAmount,
                "결제 성공",
                LocalDateTime.now()
        );
        return new ApproveClientResult(true, null, approveSuccess);
    }
}
