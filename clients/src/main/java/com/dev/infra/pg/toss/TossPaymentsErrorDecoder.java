package com.dev.infra.pg.toss;

import com.dev.infra.pg.toss.response.Error;
import com.dev.infra.pg.toss.response.TossPaymentsFailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
public class TossPaymentsErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        try (InputStream body = response.body().asInputStream()) {
            TossPaymentsFailResponse tossPaymentsFailResponse = objectMapper.readValue(body, TossPaymentsFailResponse.class);
            return new TossPaymentsException(tossPaymentsFailResponse);
        } catch (IOException e) {
            log.error("[PG] 토스페이먼츠 에러 객체 매핑에 실패했습니다. PG사에 문의하세요. Exception: {}", e.getMessage());
            Error error = new Error("", "결제 승인 요청 오류 발생");
            return new TossPaymentsException(new TossPaymentsFailResponse("", "", error));
        }
    }
}
