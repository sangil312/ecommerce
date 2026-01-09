package com.dev.infra.pg.toss;

import com.dev.infra.pg.toss.response.TossPaymentsFailResponse;
import lombok.Getter;

@Getter
public class TossPaymentsException extends RuntimeException {
    private final TossPaymentsFailResponse tossPaymentsFailResponse;

    public TossPaymentsException(TossPaymentsFailResponse tossPaymentsFailResponse) {
        this.tossPaymentsFailResponse = tossPaymentsFailResponse;
    }
}
