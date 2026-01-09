package com.dev.ecommerce.infra.pg.response;

import com.dev.ecommerce.service.payment.response.ApproveResult;
import com.dev.ecommerce.service.payment.response.ApproveFail;


public record PGApproveFailResponse(
        String version,
        String traceId,
        PGErrorResponse error
) {
    public ApproveResult toPaymentResult() {
        ApproveFail approveFail = new ApproveFail(error.code(), error.message());
        return new ApproveResult(false, approveFail, null);
    }
}
