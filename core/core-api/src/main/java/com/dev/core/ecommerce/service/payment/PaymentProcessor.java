package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.common.error.ApiException;
import com.dev.core.ecommerce.common.error.ErrorType;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.domain.payment.TransactionHistory;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.ecommerce.repository.payment.TransactionHistoryRepository;
import com.dev.core.ecommerce.service.order.OrderReader;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.enums.payment.PaymentMethod;
import com.dev.core.enums.payment.PaymentStatus;
import com.dev.core.enums.payment.TransactionType;
import com.dev.infra.pg.dto.ApproveFail;
import com.dev.infra.pg.dto.ApproveSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentProcessor {
    private final OrderReader orderReader;
    private final PaymentRepository paymentRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public Payment validatePayment(User user, String orderKey, BigDecimal amount) {
        Order order = orderReader.find(user, orderKey, OrderStatus.CREATED);

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        if (!payment.getUserId().equals(order.getUserId())) throw new ApiException(ErrorType.PAYMENT_NOT_FOUND);
        if (!payment.getStatus().equals(PaymentStatus.READY)) throw new ApiException(ErrorType.PAYMENT_ALREADY_PAID);
        if (!payment.getAmount().equals(amount)) throw new ApiException(ErrorType.PAYMENT_AMOUNT_MISMATCH);

        return payment;
    }

    @Transactional
    public void approveSuccess(User user, Payment validPayment, ApproveSuccess result) {
        Payment payment = paymentRepository.findById(validPayment.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));
        payment.success(result.externalPaymentKey(), PaymentMethod.CARD);

        Order order = orderReader.find(user, result.orderKey(), OrderStatus.CREATED);
        order.paid();

        TransactionHistory transactionHistory = TransactionHistory.create(
                payment.getUserId(),
                payment.getOrderId(),
                payment.getId(),
                TransactionType.PAYMENT,
                result.externalPaymentKey(),
                result.amount(),
                "결제 성공",
                result.approvedAt()
        );

        transactionHistoryRepository.save(transactionHistory);
    }

    @Transactional
    public void approveFail(Payment validPayment, String externalPaymentKey, ApproveFail result) {
        Payment payment = paymentRepository.findById(validPayment.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));
        payment.pending(externalPaymentKey, PaymentMethod.CARD);

        TransactionHistory transactionHistory = TransactionHistory.create(
                payment.getUserId(),
                payment.getOrderId(),
                payment.getId(),
                TransactionType.PAYMENT_FAIL,
                "",
                payment.getAmount(),
                "[" + result.code() + "] " + result.message(),
                LocalDateTime.now()
        );

        transactionHistoryRepository.save(transactionHistory);
    }
}
