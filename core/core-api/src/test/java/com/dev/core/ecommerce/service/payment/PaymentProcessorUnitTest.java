package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.domain.payment.Payment;
import com.dev.core.enums.payment.PaymentMethod;
import com.dev.core.ecommerce.service.payment.dto.PaymentApproveFail;
import com.dev.core.ecommerce.service.payment.dto.PaymentApproveResult;
import com.dev.core.ecommerce.service.payment.dto.PaymentApproveSuccess;
import com.dev.infra.pg.PGClient;
import com.dev.infra.pg.dto.ApproveFail;
import com.dev.infra.pg.dto.ApproveResult;
import com.dev.infra.pg.dto.ApproveSuccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PaymentProcessorUnitTest {

    @Mock
    private PGClient pgClient;

    @Mock
    private PaymentWriter paymentWriter;

    @Mock
    private PaymentPostProcessor paymentPostProcessor;

    @InjectMocks
    private PaymentProcessor paymentProcessor;

    @Test
    @DisplayName("PG 승인 요청 성공: success 결과 매핑")
    void requestApproveSuccess() {
        // given
        String externalPaymentKey = "ext_key";
        String orderKey = "order_123";
        BigDecimal amount = BigDecimal.valueOf(10_000);

        ApproveSuccess approveSuccess = new ApproveSuccess(
                externalPaymentKey,
                orderKey,
                "CARD",
                amount,
                "OK",
                LocalDateTime.now()
        );
        ApproveResult approveResult = new ApproveResult(true, null, approveSuccess);

        when(pgClient.requestPaymentApprove(externalPaymentKey, orderKey, amount))
                .thenReturn(approveResult);

        // when
        PaymentApproveResult result = paymentProcessor.requestApprove(externalPaymentKey, orderKey, amount);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.success());
        assertNull(result.fail());
        assertEquals(externalPaymentKey, result.success().externalPaymentKey());
        assertEquals(orderKey, result.success().orderKey());
        assertEquals(amount, result.success().amount());

        verify(pgClient).requestPaymentApprove(externalPaymentKey, orderKey, amount);
    }

    @Test
    @DisplayName("PG 승인 요청 실패: fail 결과 매핑")
    void requestConfirmFail() {
        // given
        String externalPaymentKey = "ext_key";
        String orderKey = "order_123";
        BigDecimal amount = BigDecimal.valueOf(10_000);
        ApproveFail approveFail = new ApproveFail("ERROR", "FAIL");
        ApproveResult approveResult = new ApproveResult(false, approveFail, null);

        when(pgClient.requestPaymentApprove(externalPaymentKey, orderKey, amount))
                .thenReturn(approveResult);

        // when
        PaymentApproveResult result = paymentProcessor.requestApprove(externalPaymentKey, orderKey, amount);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.fail());
        assertNull(result.success());
        assertEquals("ERROR", result.fail().code());
        assertEquals("FAIL", result.fail().message());

        verify(pgClient).requestPaymentApprove(externalPaymentKey, orderKey, amount);
    }

    @Test
    @DisplayName("PG 승인 성공: 요청 데이터와 일치 시 approveSuccess 호출")
    void validatePaymentConfirmSuccess() {
        // given
        Payment payment = mock(Payment.class);
        String orderKey = "order_123";
        String externalPaymentKey = "ext_key";
        BigDecimal amount = BigDecimal.valueOf(10_000);

        PaymentApproveSuccess paymentApproveSuccess = new PaymentApproveSuccess(
                externalPaymentKey, orderKey, PaymentMethod.CARD, amount, LocalDateTime.now()
        );

        PaymentApproveResult paymentApproveResult = new PaymentApproveResult(true, paymentApproveSuccess, null);

        // when
        paymentProcessor.process(payment, orderKey, externalPaymentKey, amount, paymentApproveResult);

        // then
        verify(paymentWriter).approveSuccess(payment.getUserId(), payment, paymentApproveSuccess);
        verify(paymentPostProcessor).processSuccess(any(), any(), any());
        verify(paymentWriter, never()).approveFail(any(), any(), any());
        verify(paymentWriter, never()).approveMismatch(any(), any(), any());
    }

    @Test
    @DisplayName("PG 승인 성공: orderKey 불일치 시 approveMismatch 호출 및 예외 발생")
    void validatePaymentConfirmOrderKeyMismatch() {
        // given
        Payment payment = mock(Payment.class);
        PaymentApproveSuccess paymentApproveSuccess = new PaymentApproveSuccess(
                "ext_key", "wrong_order_key", PaymentMethod.CARD,
                BigDecimal.valueOf(10_000), LocalDateTime.now()
        );
        PaymentApproveResult paymentApproveResult = new PaymentApproveResult(true, paymentApproveSuccess, null);

        // when then
        assertThatThrownBy(() ->
                paymentProcessor.process(
                        payment,
                        "order_123",
                        "ext_key",
                        BigDecimal.valueOf(10_000), paymentApproveResult
                )
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_APPROVE_MISMATCH);

        verify(paymentWriter).approveMismatch(payment, "ext_key", paymentApproveSuccess);
        verify(paymentWriter, never()).approveSuccess(any(), any(), any());
        verify(paymentWriter, never()).approveFail(any(), any(), any());
    }

    @Test
    @DisplayName("PG 승인 성공: amount 불일치 시 approveMismatch 호출 및 예외 발생")
    void validatePaymentConfirmAmountMismatch() {
        // given
        Payment payment = mock(Payment.class);
        BigDecimal mismatchAmount = BigDecimal.valueOf(5_000);

        PaymentApproveSuccess paymentApproveSuccess = new PaymentApproveSuccess(
                "ext_key",
                "order_123",
                PaymentMethod.CARD,
                mismatchAmount, LocalDateTime.now()
        );

        PaymentApproveResult paymentApproveResult = new PaymentApproveResult(true, paymentApproveSuccess, null);

        // when then
        assertThatThrownBy(() ->
                paymentProcessor.process(
                        payment,
                        "order_123",
                        "ext_key",
                        BigDecimal.valueOf(10_000), paymentApproveResult
                )
        )
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_APPROVE_MISMATCH);

        verify(paymentWriter).approveMismatch(payment, "ext_key", paymentApproveSuccess);
        verify(paymentWriter, never()).approveSuccess(any(), any(), any());
        verify(paymentWriter, never()).approveFail(any(), any(), any());
    }

    @Test
    @DisplayName("PG 승인 실패: approveFail 호출")
    void validatePaymentConfirmFail() {
        // given
        Payment payment = mock(Payment.class);
        PaymentApproveFail paymentApproveFail = new PaymentApproveFail("ERROR", "?쒕룄珥덇낵");
        PaymentApproveResult paymentApproveResult = new PaymentApproveResult(false, null, paymentApproveFail);

        // when
        paymentProcessor.process(
                payment,
                "order_123",
                "ext_key",
                BigDecimal.valueOf(10_000), paymentApproveResult
        );

        // then
        verify(paymentWriter).approveFail(payment, "ext_key", paymentApproveFail);
        verify(paymentWriter, never()).approveSuccess(any(), any(), any());
    }
}
