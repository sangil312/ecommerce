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
import com.dev.core.enums.payment.TransactionType;
import com.dev.infra.pg.dto.ConfirmFail;
import com.dev.infra.pg.dto.ConfirmSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmHandler {
    private final OrderReader orderReader;
    private final PaymentRepository paymentRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Transactional
    public void success(User user, Payment validPayment, ConfirmSuccess result) {
        log.info("[PG] 결제 승인 성공 - {}", result.toString());
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
    public void confirmDataMismatch(Payment validPayment, String externalPaymentKey, ConfirmSuccess result) {
        log.warn("[PG] 결제 승인 정보 불일치 - {}", result.toString());
        Payment payment = paymentRepository.findById(validPayment.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        payment.error(externalPaymentKey, PaymentMethod.CARD);

        TransactionHistory transactionHistory = TransactionHistory.create(
                payment.getUserId(),
                payment.getOrderId(),
                payment.getId(),
                TransactionType.PAYMENT_FAIL,
                externalPaymentKey,
                payment.getAmount(),
                "[PG] 결제 승인 정보 불일치 - " + result,
                LocalDateTime.now()
        );

        transactionHistoryRepository.save(transactionHistory);
    }

    @Transactional
    public void fail(Payment validPayment, String externalPaymentKey, ConfirmFail result) {
        log.info("[PG] 결제 승인 실패 - {}", result.toString());
        Payment payment = paymentRepository.findById(validPayment.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        payment.pending(externalPaymentKey, PaymentMethod.CARD);

        TransactionHistory transactionHistory = TransactionHistory.create(
                payment.getUserId(),
                payment.getOrderId(),
                payment.getId(),
                TransactionType.PAYMENT_FAIL,
                externalPaymentKey,
                payment.getAmount(),
                "[" + result.code() + "] " + result.message(),
                LocalDateTime.now()
        );

        transactionHistoryRepository.save(transactionHistory);
    }
}
