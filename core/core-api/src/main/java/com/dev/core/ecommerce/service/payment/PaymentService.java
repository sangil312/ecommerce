package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.service.payment.dto.PaymentConfirmResult;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.ecommerce.service.order.OrderReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentWriter paymentWriter;
    private final OrderReader orderReader;
    private final PaymentProcessor  paymentProcessor;

    public Long create(User user, String orderKey) {
        Order order = orderReader.findOrder(user, orderKey, OrderStatus.CREATED);
        return paymentWriter.paymentCreate(order);
    }

    public PaymentConfirmResult success(
            User user,
            String orderKey,
            String externalPaymentKey,
            BigDecimal amount
    ) {
        var validPayment = paymentProcessor.validatePayment(user, orderKey, amount);

        var paymentConfirmResult = paymentProcessor.requestConfirm(externalPaymentKey, orderKey, amount);

        paymentProcessor.validatePaymentConfirm(user, validPayment, orderKey, externalPaymentKey, amount, paymentConfirmResult);

        return paymentConfirmResult;
    }

    public void fail(User user, String orderKey, String code, String message) {
        Order order = orderReader.findOrder(user, orderKey, OrderStatus.CREATED);
        paymentWriter.callBackFail(order, code, message);
    }
}
