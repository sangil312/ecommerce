package com.dev.ecommerce.service.payment;

import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.common.error.ApiException;
import com.dev.ecommerce.common.error.ErrorType;
import com.dev.ecommerce.domain.order.Order;
import com.dev.ecommerce.domain.order.OrderStatus;
import com.dev.ecommerce.domain.payment.Payment;
import com.dev.ecommerce.domain.payment.PaymentMethod;
import com.dev.ecommerce.domain.payment.PaymentStatus;
import com.dev.ecommerce.domain.payment.TransactionHistory;
import com.dev.ecommerce.domain.payment.TransactionType;
import com.dev.ecommerce.repository.payment.PaymentRepository;
import com.dev.ecommerce.repository.payment.TransactionHistoryRepository;
import com.dev.ecommerce.service.order.OrderReader;
import com.dev.ecommerce.service.payment.response.ApproveFail;
import com.dev.ecommerce.service.payment.response.ApproveSuccess;
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
