package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.common.error.ErrorType;
import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.domain.payment.TransactionHistory;
import com.dev.core.ecommerce.repository.order.OrderItemRepository;
import com.dev.core.ecommerce.repository.payment.TransactionHistoryRepository;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.enums.payment.PaymentMethod;
import com.dev.core.enums.payment.PaymentStatus;
import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.common.error.ApiException;
import com.dev.core.ecommerce.domain.order.Order;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.ecommerce.repository.order.OrderRepository;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.enums.payment.TransactionType;
import com.dev.infra.pg.dto.ConfirmFail;
import com.dev.infra.pg.dto.ConfirmSuccess;
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

    private User testUser;
    private Order testOrder;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testUser = new User(1L);
        testOrder = orderRepository.save(Order.create(testUser.id(), BigDecimal.valueOf(1000)));
        testPayment = paymentRepository.save(
                Payment.create(testUser.id(), testOrder.getId(), BigDecimal.valueOf(10000))
        );

        orderItemRepository.save(
                OrderItem.create(
                        testOrder.getId(),
                        1L,
                        1L,
                        "테스트 상품",
                        BigDecimal.valueOf(10000),
                        BigDecimal.valueOf(10000)
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
    @DisplayName("결제 생성: 결제 성공 상태(PaymentStatus.SUCCESS)인 결제일 경우 예외 발생")
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
    void paymentSuccess() {
        // given
        ConfirmSuccess confirmSuccess = new ConfirmSuccess(
                "ext_payment_key_123",
                testOrder.getOrderKey(),
                PaymentMethod.CARD,
                BigDecimal.valueOf(10000),
                "결제 성공",
                LocalDateTime.now()
        );

        // when
        paymentWriter.paymentSuccess(testUser, testPayment, confirmSuccess);

        // then 결제 상태 확인
        Payment updatedPayment = paymentRepository.findById(testPayment.getId()).orElseThrow();
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
        assertThat(transactionHistory.getUserId()).isEqualTo(testUser.id());
        assertThat(transactionHistory.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(transactionHistory.getPaymentId()).isEqualTo(testPayment.getId());
        assertThat(transactionHistory.getType()).isEqualTo(TransactionType.PAYMENT);
        assertThat(transactionHistory.getExternalPaymentKey()).isEqualTo("ext_payment_key_123");
        assertThat(transactionHistory.getAmount()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(transactionHistory.getMessage()).isEqualTo("결제 성공");
    }

    @Test
    @DisplayName("결제 승인 성공: 존재하지 않는 결제면 예외 발생")
    void paymentSuccessWithPaymentNotFound() {
        // given
        Payment deletedPayment = Payment.create(testUser.id(), testOrder.getId(), BigDecimal.valueOf(10000));
        deletedPayment = paymentRepository.save(deletedPayment);
        paymentRepository.delete(deletedPayment);

        ConfirmSuccess confirmSuccess = new ConfirmSuccess(
                "ext_payment_key_123",
                testOrder.getOrderKey(),
                PaymentMethod.CARD,
                BigDecimal.valueOf(10000),
                "결제 성공",
                LocalDateTime.now()
        );

        Payment invalidPayment = deletedPayment;

        // when then
        assertThatThrownBy(() ->
                paymentWriter.paymentSuccess(testUser, invalidPayment, confirmSuccess)
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);
    }


    @Test
    @DisplayName("결제 승인 정보 불일치: 결제 상태 ERROR, 결제 승인 내역 생성")
    void paymentConfirmMismatch() {
        // given
        String externalPaymentKey = "ext_payment_key_mismatch";
        BigDecimal mismatchAmount = BigDecimal.valueOf(5000);

        ConfirmSuccess mismatchSuccess = new ConfirmSuccess(
                externalPaymentKey,
                "wrong_order_key",
                PaymentMethod.CARD,
                mismatchAmount,
                "결제 성공",
                LocalDateTime.now()
        );

        // when
        paymentWriter.paymentMismatch(testPayment, externalPaymentKey, mismatchSuccess);

        // then 결제 상태 확인
        Payment updatedPayment = paymentRepository.findById(testPayment.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.ERROR);
        assertThat(updatedPayment.getExternalPaymentKey()).isEqualTo(externalPaymentKey);
        assertThat(updatedPayment.getMethod()).isEqualTo(PaymentMethod.CARD);

        // then 거래 내역 확인
        TransactionHistory transactionHistory = transactionHistoryRepository
                .findByPaymentId(updatedPayment.getId()).orElseThrow();
        assertThat(transactionHistory.getUserId()).isEqualTo(testUser.id());
        assertThat(transactionHistory.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(transactionHistory.getPaymentId()).isEqualTo(testPayment.getId());
        assertThat(transactionHistory.getType()).isEqualTo(TransactionType.PAYMENT_FAIL);
        assertThat(transactionHistory.getExternalPaymentKey()).isEqualTo(externalPaymentKey);
        assertThat(transactionHistory.getAmount()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(transactionHistory.getMessage()).contains("[PG] 결제 승인 정보 불일치 - " + mismatchSuccess);
    }

    @Test
    @DisplayName("결제 승인 정보 불일치 시 존재하지 않는 결제면 예외 발생")
    void paymentConfirmMismatchWithPaymentNotFound() {
        // given
        Payment deletedPayment = Payment.create(testUser.id(), testOrder.getId(), BigDecimal.valueOf(10000));
        deletedPayment = paymentRepository.save(deletedPayment);
        paymentRepository.delete(deletedPayment);

        ConfirmSuccess mismatchSuccess = new ConfirmSuccess(
                "ext_key",
                "wrong_order_key",
                PaymentMethod.CARD,
                BigDecimal.valueOf(5000),
                "결제 성공",
                LocalDateTime.now()
        );

        Payment invalidPayment = deletedPayment;

        // when then
        assertThatThrownBy(() ->
                paymentWriter.paymentMismatch(invalidPayment, "ext_key", mismatchSuccess)
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);
    }


    @Test
    @DisplayName("결제 승인 실패: 결제 상태 PENDING, 결제 승인 실패 내역 생성")
    void paymentFail() {
        // given
        String externalPaymentKey = "ext_payment_key_fail";
        ConfirmFail confirmFail = new ConfirmFail(
                "INVALID_STOPPED_CARD",
                "정지된 카드 입니다."
        );

        // when
        paymentWriter.paymentFail(testPayment, externalPaymentKey, confirmFail);

        // then 결제 상태 확인
        Payment updatedPayment = paymentRepository.findById(testPayment.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(updatedPayment.getExternalPaymentKey()).isEqualTo(externalPaymentKey);

        // then 거래 내역 확인
        TransactionHistory transactionHistory = transactionHistoryRepository
                .findByPaymentId(updatedPayment.getId()).orElseThrow();
        assertThat(transactionHistory.getUserId()).isEqualTo(testUser.id());
        assertThat(transactionHistory.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(transactionHistory.getPaymentId()).isEqualTo(testPayment.getId());
        assertThat(transactionHistory.getType()).isEqualTo(TransactionType.PAYMENT_FAIL);
        assertThat(transactionHistory.getExternalPaymentKey()).isEqualTo(externalPaymentKey);
        assertThat(transactionHistory.getAmount()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(transactionHistory.getMessage()).isEqualTo("[INVALID_STOPPED_CARD] 정지된 카드 입니다.");
    }

    @Test
    @DisplayName("결제 승인 실패: 존재하지 않는 결제면 예외 발생")
    void paymentFailWithPaymentNotFound() {
        // given
        Payment deletedPayment = Payment.create(testUser.id(), testOrder.getId(), BigDecimal.valueOf(10000));
        deletedPayment = paymentRepository.save(deletedPayment);
        paymentRepository.delete(deletedPayment);

        ConfirmFail confirmFail = new ConfirmFail(
                "INVALID_STOPPED_CARD",
                "정지된 카드 입니다."
        );

        Payment invalidPayment = deletedPayment;

        // when then
        assertThatThrownBy(() ->
                paymentWriter.paymentFail(invalidPayment, "ext_key", confirmFail)
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

        // when
        paymentWriter.callBackFail(testOrder, code, message);

        // then 결제 상태 확인
        Payment updatedPayment = paymentRepository.findById(testPayment.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.FAIL);

        // then 거래 내역 확인
        TransactionHistory transactionHistory = transactionHistoryRepository
                .findByPaymentId(updatedPayment.getId()).orElseThrow();
        assertThat(transactionHistory.getUserId()).isEqualTo(testUser.id());
        assertThat(transactionHistory.getOrderId()).isEqualTo(testOrder.getId());
        assertThat(transactionHistory.getPaymentId()).isEqualTo(testPayment.getId());
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
                Order.create(testUser.id(), BigDecimal.valueOf(5000))
        );

        // when then
        assertThatThrownBy(() ->
                paymentWriter.callBackFail(orderWithoutPayment, "ERROR_CODE", "에러 메시지")
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_NOT_FOUND);
    }
}