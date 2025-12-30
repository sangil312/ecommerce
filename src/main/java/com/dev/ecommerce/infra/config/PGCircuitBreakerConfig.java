package com.dev.ecommerce.infra.config;

import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PGCircuitBreakerConfig {

    @Bean
    public ReactiveCircuitBreaker pgApproveCircuitBreaker(
            ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory
    ) {
        return circuitBreakerFactory.create("pg");
    }
}
