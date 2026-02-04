package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.service.payment.dto.PaymentApproveFail;
import com.dev.core.ecommerce.service.payment.dto.PaymentApproveSuccess;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.domain.payment.TransactionHistory;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.ecommerce.repository.payment.TransactionHistoryRepository;
import com.dev.core.ecommerce.service.order.OrderReader;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.enums.payment.PaymentStatus;
import com.dev.core.enums.payment.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentWriter {
    private final PaymentRepository paymentRepository;
    private final OrderReader orderReader;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Transactional
    public Long paymentCreate(Order order) {
        if (paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)) {
            throw new ApiException(ErrorType.PAYMENT_ALREADY_PAID);
        }

        var payment = Payment.create(order.getUserId(), order.getId(), order.getTotalPrice());
        return paymentRepository.save(payment).getId();
    }

    @Transactional
    public void approveSuccess(Long userId, Payment validPayment, PaymentApproveSuccess result) {
        log.info("[PG] 결제 승인 성공 - {}", result.toString());
        var payment = paymentRepository.findById(validPayment.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        payment.success(result.externalPaymentKey(), result.method());

        var order = orderReader.findOrder(userId, result.orderKey(), OrderStatus.CREATED);

        order.paid();

        var transactionHistory = TransactionHistory.create(
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
    public void approveMismatch(Payment validPayment, String externalPaymentKey, PaymentApproveSuccess result) {
        log.warn("[PG] 결제 승인 정보 불일치 - {}", result.toString());
        var payment = paymentRepository.findById(validPayment.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        payment.error(externalPaymentKey, result.method());

        var transactionHistory = TransactionHistory.create(
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
    public void approveFail(Payment validPayment, String externalPaymentKey, PaymentApproveFail result) {
        log.info("[PG] 결제 승인 실패 - {}", result.toString());
        var payment = paymentRepository.findById(validPayment.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        payment.pending(externalPaymentKey);

        var transactionHistory = TransactionHistory.create(
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

    @Transactional
    public void callBackFail(Order order, String code, String message) {
        log.info("[PG] 결제 요청 실패 - orderKey: {}, code: {}, message: {}", order.getOrderKey(), code, message);
        var payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        payment.fail();

        var transactionHistory = TransactionHistory.create(
                payment.getUserId(),
                payment.getOrderId(),
                payment.getId(),
                TransactionType.PAYMENT,
                "",
                BigDecimal.valueOf(-1),
                "[" + code + "]" + message,
                LocalDateTime.now()
        );

        transactionHistoryRepository.save(transactionHistory);
    }
}
