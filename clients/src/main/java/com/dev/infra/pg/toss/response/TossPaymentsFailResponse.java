package com.dev.infra.pg.toss.response;


import com.dev.infra.pg.dto.ApproveFail;
import com.dev.infra.pg.dto.ApproveResult;

public record TossPaymentsFailResponse(
        String version,
        String traceId,
        Error error
) {
    public ApproveResult toPaymentResult() {
        ApproveFail approveFail = new ApproveFail(error.code(), error.message());
        return new ApproveResult(false, approveFail, null);
    }
}
