package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.ecommerce.service.order.OrderReader;
import com.dev.infra.pg.dto.ApproveClientResult;
import com.dev.infra.pg.PGClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentWriter paymentWriter;
    private final OrderReader orderReader;
    private final PGClient pgClient;
    private final PaymentProcessor  paymentProcessor;

    public Long create(User user, String orderKey) {
        Order order = orderReader.find(user, orderKey, OrderStatus.CREATED);
        return paymentWriter.create(order);
    }

    public ApproveClientResult approve(User user, String orderKey, String externalPaymentKey, BigDecimal amount) {
        Payment validPayment = paymentProcessor.validatePayment(user, orderKey, amount);

        ApproveClientResult approveClientResult = pgClient.approvePayment(externalPaymentKey, orderKey, amount);

        if (approveClientResult.isSuccess()) {
            paymentProcessor.approveSuccess(user, validPayment, approveClientResult.success());
        } else {
            paymentProcessor.approveFail(validPayment, externalPaymentKey, approveClientResult.fail());
        }

        return approveClientResult;
    }
}
