package com.dev.ecommerce.infra.pg;

import com.dev.ecommerce.infra.pg.request.PGApproveRequest;
import com.dev.ecommerce.infra.pg.response.PGApproveFailResponse;
import com.dev.ecommerce.infra.pg.response.PGApproveSuccessResponse;
import com.dev.ecommerce.service.payment.response.ApproveResult;
import com.dev.ecommerce.service.payment.response.ApproveFail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * 토스페이먼츠 결제 승인 api
 * <a href="https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8">토스페이먼츠 가이드</a>
 * 가이드 참고
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PGClient {
    private final WebClient webClient;
    private final ReactiveCircuitBreaker circuitBreaker;

    public ApproveResult approvePayment(String paymentKey, String orderKey, BigDecimal amount) {
        log.info("[PG] 결제 승인 요청: paymentKey: {}, orderKey: {}, amount: {}", paymentKey, orderKey, amount);
        PGApproveRequest request = new PGApproveRequest(paymentKey, orderKey, amount);

        Mono<ApproveResult> response = webClient.post()
                // 모의 API https://beeceptor.com 사용
                .uri("https://ecommerce.free.beeceptor.com/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchangeToMono(res -> pgResponseHandler(res, orderKey));

        return circuitBreaker.run(response, ex -> exceptionHandler(ex, paymentKey, orderKey, amount))
                .block();
    }

    private Mono<ApproveResult> exceptionHandler(Throwable ex, String paymentKey, String orderKey, BigDecimal amount) {
        log.error("[PG] 결제 승인 실패: paymentKey: {}, orderKey: {}, amount: {}, Exception: {}",
                paymentKey, orderKey, amount, ex.getMessage());

        ApproveResult approveResult = new ApproveResult(
                false,
                new ApproveFail("", "결제 승인 요청 오류 발생"),
                null
        );

        return Mono.just(approveResult);
    }

    private Mono<ApproveResult> pgResponseHandler(ClientResponse response, String orderKey) {
        if (response.statusCode().isError()) {
            return response.bodyToMono(PGApproveFailResponse.class)
                    .doOnNext(failResponse -> log.warn(
                            "[PG] 결제 승인 실패: orderKey: {}, version: {}, traceId: {}, message: [{}] {}",
                            orderKey,
                            failResponse.version(),
                            failResponse.traceId(),
                            failResponse.error().code(),
                            failResponse.error().message())
                    )
                    .map(PGApproveFailResponse::toPaymentResult);
        } else {
            return response.bodyToMono(PGApproveSuccessResponse.class)
                    .map(PGApproveSuccessResponse::toPaymentResult);
        }
    }
}
