package com.dev.infra.pg.toss.response;


import com.dev.infra.pg.dto.ConfirmFail;
import com.dev.infra.pg.dto.ConfirmResult;

public record TossPaymentsFailResponse(
        String version,
        String traceId,
        Error error
) {
    public ConfirmResult toPaymentResult() {
        ConfirmFail confirmFail = new ConfirmFail(error.code(), error.message());
        return new ConfirmResult(false, confirmFail, null);
    }
}
