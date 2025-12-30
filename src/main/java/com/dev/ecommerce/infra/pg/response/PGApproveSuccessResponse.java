package com.dev.ecommerce.infra.pg.response;

import com.dev.ecommerce.domain.payment.PaymentMethod;
import com.dev.ecommerce.service.payment.response.ApproveResult;
import com.dev.ecommerce.service.payment.response.ApproveSuccess;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PGApproveSuccessResponse(
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
                PaymentMethod.fromValue(method),
                totalAmount,
                "결제 성공",
                LocalDateTime.now()
        );
        return new ApproveResult(true, null, approveSuccess);
    }
}
