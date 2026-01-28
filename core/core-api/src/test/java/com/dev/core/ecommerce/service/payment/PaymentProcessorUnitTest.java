package com.dev.core.ecommerce.service.payment;


import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.enums.payment.PaymentMethod;
import com.dev.infra.pg.dto.ConfirmFail;
import com.dev.infra.pg.dto.ConfirmResult;
import com.dev.infra.pg.dto.ConfirmSuccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PaymentProcessorUnitTest {

    @Mock
    private PaymentWriter paymentWriter;

    @InjectMocks
    private PaymentProcessor paymentProcessor;

    @Test
    @DisplayName("PG 승인 성공, 데이터 일치 시 handleSuccess 호출")
    void validateConfirmResultSuccess() {
        // given
        User user = new User(1L);
        Payment payment = Mockito.mock(Payment.class);
        String orderKey = "order_123";
        String externalPaymentKey = "ext_key";
        BigDecimal amount = BigDecimal.valueOf(10_000);

        ConfirmSuccess confirmSuccess = new ConfirmSuccess(
                externalPaymentKey, orderKey, PaymentMethod.CARD, amount, "성공", LocalDateTime.now()
        );

        ConfirmResult confirmResult = new ConfirmResult(true, null, confirmSuccess);

        // when
        paymentProcessor.validateConfirmResult(
                user, payment, orderKey, externalPaymentKey, amount, confirmResult
        );

        // then
        verify(paymentWriter).paymentSuccess(user, payment, confirmSuccess);
        verify(paymentWriter, never()).paymentFail(any(), any(), any());
        verify(paymentWriter, never()).paymentMismatch(any(), any(), any());
    }

    @Test
    @DisplayName("PG 승인 실패 시 handleFail 호출")
    void validateConfirmResultFail() {
        // given
        User user = new User(1L);
        Payment payment = Mockito.mock(Payment.class);
        ConfirmFail confirmFail = new ConfirmFail("ERROR", "실패");
        ConfirmResult confirmResult = new ConfirmResult(false, confirmFail, null);

        // when
        paymentProcessor.validateConfirmResult(
                user,
                payment,
                "order_123",
                "ext_key",
                BigDecimal.valueOf(10_000),
                confirmResult
        );

        // then
        verify(paymentWriter).paymentFail(payment, "ext_key", confirmFail);
        verify(paymentWriter, never()).paymentSuccess(any(), any(), any());
    }

    @Test
    @DisplayName("orderKey 불일치 시 handleDataMismatch 호출 후 예외 발생")
    void validateConfirmResultOrderKeyMismatch() {
        // given
        User user = new User(1L);
        Payment payment = Mockito.mock(Payment.class);
        ConfirmSuccess confirmSuccess = new ConfirmSuccess(
                "ext_key", "wrong_order_key", PaymentMethod.CARD,
                BigDecimal.valueOf(10_000), "성공", LocalDateTime.now()
        );
        ConfirmResult confirmResult = new ConfirmResult(true, null, confirmSuccess);

        // when then
        assertThatThrownBy(() ->
                paymentProcessor.validateConfirmResult(
                        user,
                        payment,
                        "order_123",
                        "ext_key",
                        BigDecimal.valueOf(10_000),
                        confirmResult
                )
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_APPROVE_MISMATCH);

        verify(paymentWriter).paymentMismatch(payment, "ext_key", confirmSuccess);
        verify(paymentWriter, never()).paymentSuccess(any(), any(), any());
        verify(paymentWriter, never()).paymentFail(any(), any(), any());
    }

    @Test
    @DisplayName("amount 불일치 시 handleDataMismatch 호출 후 예외 발생")
    void validateConfirmResultAmountMismatch() {
        // given
        User user = new User(1L);
        Payment payment = Mockito.mock(Payment.class);
        BigDecimal mismatchAmount = BigDecimal.valueOf(5_000);

        ConfirmSuccess confirmSuccess = new ConfirmSuccess(
                "ext_key",
                "order_123",
                PaymentMethod.CARD,
                mismatchAmount,
                "성공",
                LocalDateTime.now()
        );

        ConfirmResult confirmResult = new ConfirmResult(true, null, confirmSuccess);

        // when then
        assertThatThrownBy(() ->
                paymentProcessor.validateConfirmResult(
                        user,
                        payment,
                        "order_123",
                        "ext_key",
                        BigDecimal.valueOf(10_000),
                        confirmResult
                )
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_APPROVE_MISMATCH);

        verify(paymentWriter).paymentMismatch(payment, "ext_key", confirmSuccess);
        verify(paymentWriter, never()).paymentSuccess(any(), any(), any());
        verify(paymentWriter, never()).paymentFail(any(), any(), any());
    }
}