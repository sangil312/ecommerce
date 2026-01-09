package com.dev.infra.config;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        return circuitBreakerFactory.create("tossPayments");
    }
}
