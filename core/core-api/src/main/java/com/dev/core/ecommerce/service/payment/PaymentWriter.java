package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.common.error.ApiException;
import com.dev.core.ecommerce.common.error.ErrorType;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.enums.payment.PaymentStatus;
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
