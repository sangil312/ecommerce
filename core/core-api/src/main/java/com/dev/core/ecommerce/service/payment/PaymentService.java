package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.ecommerce.service.order.OrderReader;
import com.dev.infra.pg.dto.ConfirmResult;
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
        Order order = orderReader.find(user, orderKey, OrderStatus.CREATED);
        return paymentWriter.create(order);
    }

    public ConfirmResult confirm(
            User user,
            String orderKey,
            String externalPaymentKey,
            BigDecimal amount
    ) {
        Payment validPayment = paymentProcessor.validatePayment(user, orderKey, amount);

        ConfirmResult confirmResult = paymentProcessor.requestConfirm(externalPaymentKey, orderKey, amount);

        paymentProcessor.validateConfirmResult(user, validPayment, orderKey, externalPaymentKey, amount, confirmResult);

        return confirmResult;
    }
}
