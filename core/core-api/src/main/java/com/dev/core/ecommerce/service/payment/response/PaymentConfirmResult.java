package com.dev.core.ecommerce.service.payment.response;

import com.dev.core.enums.payment.PaymentMethod;
import com.dev.infra.pg.dto.ConfirmFail;
import com.dev.infra.pg.dto.ConfirmSuccess;

public record PaymentConfirmResult(
        boolean isSuccess,
        PaymentConfirmSuccess success,
        PaymentConfirmFail fail
) {
    public static PaymentConfirmResult success(ConfirmSuccess success) {
        var paymentConfirmSuccess = new PaymentConfirmSuccess(
                success.externalPaymentKey(),
                success.orderKey(),
                PaymentMethod.fromValue(success.method()),
                success.amount(),
                success.approvedAt()
        );

        return new PaymentConfirmResult(true, paymentConfirmSuccess, null);
    }

    public static PaymentConfirmResult fail(ConfirmFail fail) {
        var paymentConfirmFail = new PaymentConfirmFail(fail.code(), fail.message());
        return new PaymentConfirmResult(false, null, paymentConfirmFail);
    }
}
