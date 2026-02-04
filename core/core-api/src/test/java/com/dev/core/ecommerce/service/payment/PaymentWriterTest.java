package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.service.payment.dto.PaymentApproveFail;
import com.dev.core.ecommerce.service.payment.dto.PaymentApproveSuccess;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.domain.payment.TransactionHistory;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.payment.TransactionHistoryRepository;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.enums.payment.PaymentMethod;
import com.dev.core.enums.payment.PaymentStatus;
import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.enums.payment.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Transactional
class PaymentWriterTest extends IntegrationTestSupport {
    @Autowired
    private PaymentWriter paymentWriter;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    private final Long userId = 1L;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = orderRepository.save(Order.create(userId, BigDecimal.valueOf(1_000)));
        orderItemRepository.save(
                OrderItem.create(
                        testOrder.getId(),
                        1L,
                        1L,
                        "테스트 상품",
                        "thumbnail-url",
                        BigDecimal.valueOf(10_000),
                        BigDecimal.valueOf(10_000)
                )
        );
    }

    @Test
    @DisplayName("결제 생성")
    void paymentCreate() {
        //when
        Long paymentId = paymentWriter.paymentCreate(testOrder);

        //then
        Payment createdPayment = paymentRepository.findById(paymentId).orElseThrow();
        assertThat(createdPayment.getId()).isEqualTo(paymentId);
        assertThat(createdPayment.getUserId()).isEqualTo(testOrder.getUserId());
        assertThat(createdPayment.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(createdPayment.getAmount()).isEqualTo(testOrder.getTotalPrice());
        assertThat(createdPayment.getStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(createdPayment.getExternalPaymentKey()).isNull();
        assertThat(createdPayment.getMethod()).isNull();
        assertThat(createdPayment.getPaidAt()).isNull();
    }

    @Test
    @DisplayName("결제 생성: 결제 상태 SUCCESS일 경우 예외 발생")
    void paymentCreateWithAlreadyPaid() {
        //when
        Long paymentId = paymentWriter.paymentCreate(testOrder);

        Payment createdPayment = paymentRepository.findById(paymentId).orElseThrow();
        createdPayment.success(null, PaymentMethod.CARD);

        assertThatThrownBy(() -> paymentWriter.paymentCreate(testOrder))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_ALREADY_PAID);
    }

    @Test
    @DisplayName("결제 승인 성공: 결제 상태 SUCCESS, 주문 상태 PAID, 결제 승인 내역 생성")
    void approveSuccess() {
        // given
        PaymentApproveSuccess paymentApproveSuccess = new PaymentApproveSuccess(
                "ext_payment_key_123",
                testOrder.getOrderKey(),
                PaymentMethod.CARD,
                BigDecimal.valueOf(10_000),
                LocalDateTime.now()
        );

        Payment savedPayment = createPayment();

        // when
        paymentWriter.approveSuccess(userId, savedPayment, paymentApproveSuccess);

        // then 결제 상태 확인
        Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(updatedPayment.getExternalPaymentKey()).isEqualTo("ext_payment_key_123");
        assertThat(updatedPayment.getMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(updatedPayment.getPaidAt()).isNotNull();

        // then 주문 상태 확인
        Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAID);

        // then 거래 내역 확인
        TransactionHistory transactionHistory = transactionHistoryRepository
                .findByPaymentId(updatedPayment.getId()).orElseThrow();
        assertThat(transactionHistory.getUserId()).isEqualTo(userId);
        assertThat(transactionHistory.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(transactionHistory.getPaymentId()).isEqualTo(savedPayment.getId());
        assertThat(transactionHistory.getType()).isEqualTo(TransactionType.PAYMENT);
        assertThat(transactionHistory.getExternalPaymentKey()).isEqualTo("ext_payment_key_123");
        assertThat(transactionHistory.getAmount()).isEqualTo(BigDecimal.valueOf(10_000));
        assertThat(transactionHistory.getMessage()).isEqualTo("결제 성공");
    }

    @Test
    @DisplayName("결제 승인 성공: 존재하지 않는 결제면 예외 발생")
    void paymentSuccessWithApproveNotFound() {
        // given
        Payment deletedPayment = Payment.create(userId, testOrder.getId(), BigDecimal.valueOf(10_000));
        deletedPayment = paymentRepository.save(deletedPayment);
        paymentRepository.delete(deletedPayment);

        PaymentApproveSuccess paymentApproveSuccess = new PaymentApproveSuccess(
                "ext_payment_key_123",
                testOrder.getOrderKey(),
                PaymentMethod.CARD,
                BigDecimal.valueOf(10_000),
                LocalDateTime.now()
        );

        Payment invalidPayment = deletedPayment;

        // when then
        assertThatThrownBy(() ->
                paymentWriter.approveSuccess(userId, invalidPayment, paymentApproveSuccess)
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);
    }


    @Test
    @DisplayName("결제 승인 정보 불일치: 결제 상태 ERROR, 결제 승인 내역 생성")
    void paymentConfirmMismatch() {
        // given
        String externalPaymentKey = "ext_payment_key_123";
        BigDecimal mismatchAmount = BigDecimal.valueOf(5_000);

        PaymentApproveSuccess mismatchSuccess = new PaymentApproveSuccess(
                externalPaymentKey,
                "order_key_123",
                PaymentMethod.CARD,
                mismatchAmount,
                LocalDateTime.now()
        );

        Payment savedPayment = createPayment();

        // when
        paymentWriter.approveMismatch(savedPayment, externalPaymentKey, mismatchSuccess);

        // then 결제 상태 확인
        Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.ERROR);
        assertThat(updatedPayment.getExternalPaymentKey()).isEqualTo(externalPaymentKey);
        assertThat(updatedPayment.getMethod()).isEqualTo(PaymentMethod.CARD);

        // then 거래 내역 확인
        TransactionHistory transactionHistory = transactionHistoryRepository
                .findByPaymentId(updatedPayment.getId()).orElseThrow();
        assertThat(transactionHistory.getUserId()).isEqualTo(userId);
        assertThat(transactionHistory.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(transactionHistory.getPaymentId()).isEqualTo(savedPayment.getId());
        assertThat(transactionHistory.getType()).isEqualTo(TransactionType.PAYMENT_FAIL);
        assertThat(transactionHistory.getExternalPaymentKey()).isEqualTo(externalPaymentKey);
        assertThat(transactionHistory.getAmount()).isEqualTo(BigDecimal.valueOf(10_000));
        assertThat(transactionHistory.getMessage()).contains("[PG] 결제 승인 정보 불일치 - " + mismatchSuccess);
    }

    @Test
    @DisplayName("결제 승인 정보 불일치 시 존재하지 않는 결제면 예외 발생")
    void paymentConfirmMismatchWithPaymentNotFound() {
        // given
        Payment deletedPayment = Payment.create(userId, testOrder.getId(), BigDecimal.valueOf(10_000));
        deletedPayment = paymentRepository.save(deletedPayment);
        paymentRepository.delete(deletedPayment);

        PaymentApproveSuccess mismatchSuccess = new PaymentApproveSuccess(
                "ext_key",
                "order_key_123",
                PaymentMethod.CARD,
                BigDecimal.valueOf(5_000),
                LocalDateTime.now()
        );

        Payment invalidPayment = deletedPayment;

        // when then
        assertThatThrownBy(() ->
                paymentWriter.approveMismatch(invalidPayment, "ext_key", mismatchSuccess)
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);
    }


    @Test
    @DisplayName("결제 승인 실패: 결제 상태 PENDING, 결제 승인 실패 내역 생성")
    void approveFail() {
        // given
        String externalPaymentKey = "ext_payment_key_fail";
        PaymentApproveFail paymentApproveFail = new PaymentApproveFail("ERROR", "한도초과");

        Payment savedPayment = createPayment();

        // when
        paymentWriter.approveFail(savedPayment, externalPaymentKey, paymentApproveFail);

        // then 결제 상태 확인
        Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(updatedPayment.getExternalPaymentKey()).isEqualTo(externalPaymentKey);

        // then 거래 내역 확인
        TransactionHistory transactionHistory = transactionHistoryRepository
                .findByPaymentId(updatedPayment.getId()).orElseThrow();
        assertThat(transactionHistory.getUserId()).isEqualTo(userId);
        assertThat(transactionHistory.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(transactionHistory.getPaymentId()).isEqualTo(savedPayment.getId());
        assertThat(transactionHistory.getType()).isEqualTo(TransactionType.PAYMENT_FAIL);
        assertThat(transactionHistory.getExternalPaymentKey()).isEqualTo(externalPaymentKey);
        assertThat(transactionHistory.getAmount()).isEqualTo(BigDecimal.valueOf(10_000));
        assertThat(transactionHistory.getMessage()).isEqualTo("[ERROR] 한도초과");
    }

    @Test
    @DisplayName("결제 승인 실패: 존재하지 않는 결제면 예외 발생")
    void paymentFailWithApproveNotFound() {
        // given
        Payment deletedPayment = Payment.create(userId, testOrder.getId(), BigDecimal.valueOf(10_000));
        deletedPayment = paymentRepository.save(deletedPayment);
        paymentRepository.delete(deletedPayment);

        PaymentApproveFail paymentApproveFail = new PaymentApproveFail("ERROR", "한도초과");

        Payment invalidPayment = deletedPayment;

        // when then
        assertThatThrownBy(() ->
                paymentWriter.approveFail(invalidPayment, "ext_key", paymentApproveFail)
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("결제 요청 실패: 결제 상태 FAIL, 결제 요청 실패 내역 생성")
    void callBackFail() {
        // given
        String code = "PAYMENT_REQUEST_FAILED";
        String message = "결제 요청이 실패했습니다.";
        Payment savedPayment = createPayment();

        // when
        paymentWriter.callBackFail(testOrder, code, message);

        // then 결제 상태 확인
        Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.FAIL);

        // then 거래 내역 확인
        TransactionHistory transactionHistory = transactionHistoryRepository
                .findByPaymentId(updatedPayment.getId()).orElseThrow();
        assertThat(transactionHistory.getUserId()).isEqualTo(userId);
        assertThat(transactionHistory.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(transactionHistory.getPaymentId()).isEqualTo(updatedPayment.getId());
        assertThat(transactionHistory.getType()).isEqualTo(TransactionType.PAYMENT);
        assertThat(transactionHistory.getExternalPaymentKey()).isEmpty();
        assertThat(transactionHistory.getAmount()).isEqualTo(BigDecimal.valueOf(-1));
        assertThat(transactionHistory.getMessage()).isEqualTo("[" + code + "]" + message);
    }

    @Test
    @DisplayName("결제 요청 실패: 존재하지 않는 결제면 예외 발생")
    void callBackFailWithPaymentNotFound() {
        // given
        Order orderWithoutPayment = orderRepository.save(
                Order.create(userId, BigDecimal.valueOf(5_000))
        );

        // when then
        assertThatThrownBy(() ->
                paymentWriter.callBackFail(orderWithoutPayment, "ERROR_CODE", "에러 메시지")
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);
    }

    private Payment createPayment() {
        return paymentRepository.save(
                Payment.create(userId, testOrder.getId(), BigDecimal.valueOf(10_000))
        );
    }
}
