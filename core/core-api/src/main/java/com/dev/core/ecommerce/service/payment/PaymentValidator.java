package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.ecommerce.service.order.OrderReader;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.enums.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentValidator {
    private final OrderReader orderReader;
    private final PaymentRepository paymentRepository;

    public Payment validatePayment(Long userId, String orderKey, BigDecimal amount) {
        var order = orderReader.findOrder(userId, orderKey, OrderStatus.CREATED);

        var payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        if (!payment.getUserId().equals(order.getUserId())) throw new ApiException(ErrorType.PAYMENT_NOT_FOUND);
        if (!payment.getStatus().equals(PaymentStatus.READY)) throw new ApiException(ErrorType.PAYMENT_ALREADY_PAID);
        if (!payment.getAmount().equals(amount)) throw new ApiException(ErrorType.PAYMENT_AMOUNT_MISMATCH);

        return payment;
    }
}
