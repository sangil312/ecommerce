package com.dev.core.enums.payment;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum PaymentMethod {
    CARD("카드");

    private final String value;

    public static PaymentMethod fromValue(String value) {
        return Arrays.stream(PaymentMethod.values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
