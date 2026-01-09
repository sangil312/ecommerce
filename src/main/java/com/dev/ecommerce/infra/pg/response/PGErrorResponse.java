package com.dev.ecommerce.infra.pg.response;

public record PGErrorResponse(
        String code,
        String message
) {
}
