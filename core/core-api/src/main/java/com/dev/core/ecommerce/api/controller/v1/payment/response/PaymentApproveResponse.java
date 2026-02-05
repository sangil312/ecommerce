package com.dev.core.ecommerce.api.controller.v1.payment.response;

import com.dev.core.ecommerce.service.payment.dto.PaymentApproveResult;

public record PaymentApproveResponse(
        Boolean isSuccess,
        String code,
        String message
) {
    public static PaymentApproveResponse of(PaymentApproveResult result) {
        return result.isSuccess()
                ? new PaymentApproveResponse(true, null, null)
                : new PaymentApproveResponse(false, result.fail().code(), result.fail().message());
    }
}
