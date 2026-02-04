package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.service.payment.dto.PaymentApproveResult;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentWriter paymentWriter;
    private final PaymentValidator paymentValidator;
    private final PaymentProcessor paymentProcessor;

    public Long createPayment(Order order) {
        return paymentWriter.paymentCreate(order);
    }

    public PaymentApproveResult success(
            User user,
            String orderKey,
            String externalPaymentKey,
            BigDecimal amount
    ) {
        var validPayment = paymentValidator.validatePayment(user.id(), orderKey, amount);

        var paymentApproveResult = paymentProcessor.requestApprove(externalPaymentKey, orderKey, amount);

        paymentProcessor.process(
                validPayment,
                orderKey,
                externalPaymentKey,
                amount,
                paymentApproveResult
        );

        return paymentApproveResult;
    }

    public void fail(Order order, String code, String message) {
        paymentWriter.callBackFail(order, code, message);
    }
}
