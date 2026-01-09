package com.dev.infra.config;

import com.dev.infra.pg.toss.TossPaymentsErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.dev.infra")
@Configuration
public class ClientConfig {

    @Bean
    public ErrorDecoder tossPaymentsErrorDecoder(ObjectMapper objectMapper) {
        return new TossPaymentsErrorDecoder(objectMapper);
    }
}
