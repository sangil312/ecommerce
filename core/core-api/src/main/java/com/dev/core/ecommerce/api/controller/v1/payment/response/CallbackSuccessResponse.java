package com.dev.core.ecommerce.api.controller.v1.payment.response;

public record CallbackSuccessResponse(
        String code,
        String message
) {
}
