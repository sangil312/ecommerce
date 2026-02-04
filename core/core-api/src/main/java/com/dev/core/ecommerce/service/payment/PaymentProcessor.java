package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.service.payment.dto.PaymentApproveResult;
import com.dev.core.ecommerce.service.payment.dto.PaymentApproveSuccess;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.infra.pg.PGClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentProcessor {
    private final PGClient pgClient;
    private final PaymentWriter paymentWriter;
    private final PaymentPostProcessor paymentPostProcessor;

    public PaymentApproveResult requestApprove(String paymentKey, String orderKey, BigDecimal amount) {
        var approveResult = pgClient.requestPaymentApprove(paymentKey, orderKey, amount);

        return approveResult.isSuccess()
                ? PaymentApproveResult.success(approveResult.success())
                : PaymentApproveResult.fail(approveResult.fail());
    }

    public void process(
            Payment payment,
            String orderKey,
            String externalPaymentKey,
            BigDecimal amount,
            PaymentApproveResult approveResult
    ) {
        if (approveResult.isSuccess()) {
            PaymentApproveSuccess success = approveResult.success();

            if (!success.orderKey().equals(orderKey) || !success.amount().equals(amount)) {
                paymentWriter.approveMismatch(payment, externalPaymentKey, success);
                throw new ApiException(ErrorType.PAYMENT_APPROVE_MISMATCH);
            }

            paymentWriter.approveSuccess(payment.getUserId(), payment, success);

            paymentPostProcessor.processSuccess(payment.getUserId(), payment.getId(), orderKey);
        } else {
            paymentWriter.approveFail(payment, externalPaymentKey, approveResult.fail());
        }
    }
}
