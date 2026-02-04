package com.dev.core.ecommerce.service.payment.dto;

import com.dev.core.enums.payment.PaymentMethod;
import com.dev.infra.pg.dto.ApproveFail;
import com.dev.infra.pg.dto.ApproveSuccess;

public record PaymentApproveResult(
        boolean isSuccess,
        PaymentApproveSuccess success,
        PaymentApproveFail fail
) {
    public static PaymentApproveResult success(ApproveSuccess success) {
        var paymentConfirmSuccess = new PaymentApproveSuccess(
                success.externalPaymentKey(),
                success.orderKey(),
                PaymentMethod.fromValue(success.method()),
                success.amount(),
                success.approvedAt()
        );
        return new PaymentApproveResult(true, paymentConfirmSuccess, null);
    }

    public static PaymentApproveResult fail(ApproveFail fail) {
        var paymentConfirmFail = new PaymentApproveFail(fail.code(), fail.message());
        return new PaymentApproveResult(false, null, paymentConfirmFail);
    }
}
