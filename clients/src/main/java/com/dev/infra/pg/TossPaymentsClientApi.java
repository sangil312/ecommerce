package com.dev.infra.pg;


import com.dev.infra.config.ClientConfig;
import com.dev.infra.pg.toss.request.ConfirmRequest;
import com.dev.infra.pg.toss.response.TossPaymentsConfirmSuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tossPayments",
        url = "${pg.toss.api.base-url}",
        configuration = ClientConfig.class
)
public interface TossPaymentsClientApi {

    @PostMapping(value = "/v1/payments/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    TossPaymentsConfirmSuccessResponse confirm(@RequestBody ConfirmRequest request);
}
