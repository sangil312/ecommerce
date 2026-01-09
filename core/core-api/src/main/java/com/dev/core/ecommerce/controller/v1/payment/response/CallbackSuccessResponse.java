package com.dev.core.ecommerce.controller.v1.payment.response;

public record CallbackSuccessResponse(
        String code,
        String message
) {
}
