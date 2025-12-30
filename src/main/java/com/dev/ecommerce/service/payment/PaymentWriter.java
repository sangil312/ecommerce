package com.dev.ecommerce.service.payment;

import com.dev.ecommerce.common.error.ApiException;
import com.dev.ecommerce.common.error.ErrorType;
import com.dev.ecommerce.domain.order.Order;
import com.dev.ecommerce.domain.payment.Payment;
import com.dev.ecommerce.domain.payment.PaymentStatus;
import com.dev.ecommerce.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentWriter {
    private final PaymentRepository paymentRepository;

    @Transactional
    public Long create(Order order) {
        if (paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)) {
            throw new ApiException(ErrorType.PAYMENT_ALREADY_PAID);
        }

        Payment payment = Payment.create(order.getUserId(), order.getId(), order.getTotalPrice());
        return paymentRepository.save(payment).getId();
    }
}
