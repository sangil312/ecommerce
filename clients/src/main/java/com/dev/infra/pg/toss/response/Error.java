package com.dev.infra.pg.toss.response;

public record Error(
        String code,
        String message
) {
}
