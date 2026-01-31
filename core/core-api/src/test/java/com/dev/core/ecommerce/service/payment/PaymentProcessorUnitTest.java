package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.enums.payment.PaymentMethod;
import com.dev.core.ecommerce.service.payment.dto.PaymentConfirmFail;
import com.dev.core.ecommerce.service.payment.dto.PaymentConfirmResult;
import com.dev.core.ecommerce.service.payment.dto.PaymentConfirmSuccess;
import com.dev.infra.pg.PGClient;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PaymentProcessorUnitTest {

    @Mock
    private PGClient pgClient;

    @Mock
    private PaymentWriter paymentWriter;

    @InjectMocks
    private PaymentProcessor paymentProcessor;

    @Test
    @DisplayName("PG 승인 요청 성공: success 결과 매핑")
    void requestConfirmSuccess() {
        // given
        String externalPaymentKey = "ext_key";
        String orderKey = "order_123";
        BigDecimal amount = BigDecimal.valueOf(10_000);

        ConfirmSuccess confirmSuccess = new ConfirmSuccess(
                externalPaymentKey,
                orderKey,
                "CARD",
                amount,
                "OK",
                LocalDateTime.now()
        );
        ConfirmResult confirmResult = new ConfirmResult(true, null, confirmSuccess);

        when(pgClient.requestPaymentConfirm(externalPaymentKey, orderKey, amount))
                .thenReturn(confirmResult);

        // when
        PaymentConfirmResult result = paymentProcessor.requestConfirm(externalPaymentKey, orderKey, amount);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.success());
        assertNull(result.fail());
        assertEquals(externalPaymentKey, result.success().externalPaymentKey());
        assertEquals(orderKey, result.success().orderKey());
        assertEquals(amount, result.success().amount());
        verify(pgClient).requestPaymentConfirm(externalPaymentKey, orderKey, amount);
    }

    @Test
    @DisplayName("PG 승인 요청 실패: fail 결과 매핑")
    void requestConfirmFail() {
        // given
        String externalPaymentKey = "ext_key";
        String orderKey = "order_123";
        BigDecimal amount = BigDecimal.valueOf(10_000);
        ConfirmFail confirmFail = new ConfirmFail("ERROR", "FAIL");
        ConfirmResult confirmResult = new ConfirmResult(false, confirmFail, null);

        when(pgClient.requestPaymentConfirm(externalPaymentKey, orderKey, amount))
                .thenReturn(confirmResult);

        // when
        PaymentConfirmResult result = paymentProcessor.requestConfirm(externalPaymentKey, orderKey, amount);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.fail());
        assertNull(result.success());
        assertEquals("ERROR", result.fail().code());
        assertEquals("FAIL", result.fail().message());
        verify(pgClient).requestPaymentConfirm(externalPaymentKey, orderKey, amount);
    }

    @Test
    @DisplayName("PG 승인 성공: 요청 데이터와 일치 시 paymentSuccess 호출")
    void validatePaymentConfirmSuccess() {
        // given
        User user = new User(1L);
        Payment payment = Mockito.mock(Payment.class);
        String orderKey = "order_123";
        String externalPaymentKey = "ext_key";
        BigDecimal amount = BigDecimal.valueOf(10_000);

        PaymentConfirmSuccess paymentConfirmSuccess = new PaymentConfirmSuccess(
                externalPaymentKey, orderKey, PaymentMethod.CARD, amount, LocalDateTime.now()
        );

        PaymentConfirmResult paymentConfirmResult = new PaymentConfirmResult(true, paymentConfirmSuccess, null);

        // when
        paymentProcessor.validatePaymentConfirm(
                user, payment, orderKey, externalPaymentKey, amount, paymentConfirmResult
        );

        // then
        verify(paymentWriter).paymentSuccess(user, payment, paymentConfirmSuccess);
        verify(paymentWriter, never()).paymentFail(any(), any(), any());
        verify(paymentWriter, never()).paymentMismatch(any(), any(), any());
    }

    @Test
    @DisplayName("PG 승인 성공: orderKey 불일치 시 paymentMismatch 호출 및 예외 발생")
    void validatePaymentConfirmOrderKeyMismatch() {
        // given
        User user = new User(1L);
        Payment payment = Mockito.mock(Payment.class);
        PaymentConfirmSuccess paymentConfirmSuccess = new PaymentConfirmSuccess(
                "ext_key", "wrong_order_key", PaymentMethod.CARD,
                BigDecimal.valueOf(10_000), LocalDateTime.now()
        );
        PaymentConfirmResult paymentConfirmResult = new PaymentConfirmResult(true, paymentConfirmSuccess, null);

        // when then
        assertThatThrownBy(() ->
                paymentProcessor.validatePaymentConfirm(
                        user,
                        payment,
                        "order_123",
                        "ext_key",
                        BigDecimal.valueOf(10_000), paymentConfirmResult
                )
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_APPROVE_MISMATCH);

        verify(paymentWriter).paymentMismatch(payment, "ext_key", paymentConfirmSuccess);
        verify(paymentWriter, never()).paymentSuccess(any(), any(), any());
        verify(paymentWriter, never()).paymentFail(any(), any(), any());
    }

    @Test
    @DisplayName("PG 승인 성공: amount 불일치 시 paymentMismatch 호출 및 예외 발생")
    void validatePaymentConfirmAmountMismatch() {
        // given
        User user = new User(1L);
        Payment payment = Mockito.mock(Payment.class);
        BigDecimal mismatchAmount = BigDecimal.valueOf(5_000);

        PaymentConfirmSuccess paymentConfirmSuccess = new PaymentConfirmSuccess(
                "ext_key",
                "order_123",
                PaymentMethod.CARD,
                mismatchAmount, LocalDateTime.now()
        );

        PaymentConfirmResult paymentConfirmResult = new PaymentConfirmResult(true, paymentConfirmSuccess, null);

        // when then
        assertThatThrownBy(() ->
                paymentProcessor.validatePaymentConfirm(
                        user,
                        payment,
                        "order_123",
                        "ext_key",
                        BigDecimal.valueOf(10_000), paymentConfirmResult
                )
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_APPROVE_MISMATCH);

        verify(paymentWriter).paymentMismatch(payment, "ext_key", paymentConfirmSuccess);
        verify(paymentWriter, never()).paymentSuccess(any(), any(), any());
        verify(paymentWriter, never()).paymentFail(any(), any(), any());
    }

    @Test
    @DisplayName("PG 승인 실패: paymentFail 호출")
    void validatePaymentConfirmFail() {
        // given
        User user = new User(1L);
        Payment payment = Mockito.mock(Payment.class);
        PaymentConfirmFail paymentConfirmFail = new PaymentConfirmFail("ERROR", "한도초과");
        PaymentConfirmResult paymentConfirmResult = new PaymentConfirmResult(false, null, paymentConfirmFail);

        // when
        paymentProcessor.validatePaymentConfirm(
                user,
                payment,
                "order_123",
                "ext_key",
                BigDecimal.valueOf(10_000), paymentConfirmResult
        );

        // then
        verify(paymentWriter).paymentFail(payment, "ext_key", paymentConfirmFail);
        verify(paymentWriter, never()).paymentSuccess(any(), any(), any());
    }
}

