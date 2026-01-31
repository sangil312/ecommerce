package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.service.payment.dto.PaymentConfirmResult;
import com.dev.core.ecommerce.service.payment.dto.PaymentConfirmSuccess;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.ecommerce.service.order.OrderReader;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.enums.payment.PaymentStatus;
import com.dev.infra.pg.PGClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentProcessor {
    private final OrderReader orderReader;
    private final PaymentRepository paymentRepository;
    private final PGClient pgClient;
    private final PaymentWriter paymentWriter;

    public Payment validatePayment(User user, String orderKey, BigDecimal amount) {
        var order = orderReader.findOrder(user, orderKey, OrderStatus.CREATED);

        var payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        if (!payment.getUserId().equals(order.getUserId())) throw new ApiException(ErrorType.PAYMENT_NOT_FOUND);
        if (!payment.getStatus().equals(PaymentStatus.READY)) throw new ApiException(ErrorType.PAYMENT_ALREADY_PAID);
        if (!payment.getAmount().equals(amount)) throw new ApiException(ErrorType.PAYMENT_AMOUNT_MISMATCH);

        return payment;
    }

    public PaymentConfirmResult requestConfirm(String paymentKey, String orderKey, BigDecimal amount) {
        var confirmResult = pgClient.requestPaymentConfirm(paymentKey, orderKey, amount);

        return confirmResult.isSuccess()
                ? PaymentConfirmResult.success(confirmResult.success())
                : PaymentConfirmResult.fail(confirmResult.fail());
    }

    public void validatePaymentConfirm(
            User user,
            Payment validPayment,
            String orderKey,
            String externalPaymentKey,
            BigDecimal amount,
            PaymentConfirmResult paymentConfirmResult
    ) {
        if (paymentConfirmResult.isSuccess()) {
            PaymentConfirmSuccess success = paymentConfirmResult.success();

            if (!success.orderKey().equals(orderKey) || !success.amount().equals(amount)) {
                paymentWriter.paymentMismatch(validPayment, externalPaymentKey, success);
                throw new ApiException(ErrorType.PAYMENT_APPROVE_MISMATCH);
            }

            paymentWriter.paymentSuccess(user, validPayment, success);
        } else {
            paymentWriter.paymentFail(validPayment, externalPaymentKey, paymentConfirmResult.fail());
        }
    }
}
