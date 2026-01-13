package com.dev.infra.pg;

import com.dev.infra.pg.dto.ConfirmResult;
import com.dev.infra.pg.dto.ConfirmFail;
import com.dev.infra.pg.toss.TossPaymentsException;
import com.dev.infra.pg.toss.request.ConfirmRequest;
import com.dev.infra.pg.toss.response.TossPaymentsFailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentsClient implements PGClient {
    private final TossPaymentsClientApi tossPaymentsClientApi;
    private final CircuitBreaker circuitBreaker;

    @Override
    public ConfirmResult requestPaymentConfirm(String paymentKey, String orderKey, BigDecimal amount) {
        log.info("[PG] 결제 승인 요청: paymentKey: {}, orderKey: {}, amount: {}", paymentKey, orderKey, amount);
        ConfirmRequest request = new ConfirmRequest(paymentKey, orderKey, amount);

        return circuitBreaker.run(
                () -> confirm(request),
                ex -> fallback(ex, request)
        );
    }

    private ConfirmResult confirm(ConfirmRequest request) {
        try {
            return tossPaymentsClientApi.confirm(request).toPaymentResult();
        } catch (TossPaymentsException e) {
            TossPaymentsFailResponse failResponse = e.getTossPaymentsFailResponse();

            log.warn(
                    "[PG] 결제 승인 실패: orderKey: {}, version: {}, traceId: {}, message: [{}] {}",
                    request.orderId(),
                    failResponse.version(),
                    failResponse.traceId(),
                    failResponse.error().code(),
                    failResponse.error().message());

            return failResponse.toPaymentResult();
        }
    }

    private ConfirmResult fallback(Throwable ex, ConfirmRequest request) {
        log.error("[PG] 결제 승인 실패: paymentKey: {}, orderKey: {}, amount: {}, Exception: {}",
                request.paymentKey(), request.orderId(), request.amount(), ex.getMessage());

        return new ConfirmResult(
                false,
                new ConfirmFail("", "결제 승인 요청 오류 발생"),
                null
        );
    }
}
