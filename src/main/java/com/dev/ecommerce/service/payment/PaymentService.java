package com.dev.ecommerce.service.payment;

import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.domain.order.Order;
import com.dev.ecommerce.domain.order.OrderStatus;
import com.dev.ecommerce.domain.payment.Payment;
import com.dev.ecommerce.infra.pg.PGClient;
import com.dev.ecommerce.service.order.OrderReader;
import com.dev.ecommerce.service.payment.response.ApproveResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentWriter paymentWriter;
    private final OrderReader orderReader;
    private final PGClient PGClient;
    private final PaymentProcessor  paymentProcessor;

    public Long create(User user, String orderKey) {
        Order order = orderReader.find(user, orderKey, OrderStatus.CREATED);
        return paymentWriter.create(order);
    }

    public ApproveResult approve(User user, String orderKey, String externalPaymentKey, BigDecimal amount) {
        //TODO
        // 결제 요청 검증
        // 승인 요청
        // 승인된 결제 검증
        // 승인 tx 저장
        Payment validPayment = paymentProcessor.validatePayment(user, orderKey, amount);

        ApproveResult approveResult = PGClient.approvePayment(externalPaymentKey, orderKey, amount);

        if (approveResult.isSuccess()) {
            paymentProcessor.approveSuccess(user, validPayment, approveResult.success());
        } else {
            paymentProcessor.approveFail(validPayment, externalPaymentKey, approveResult.fail());
        }

        return approveResult;
    }
}
