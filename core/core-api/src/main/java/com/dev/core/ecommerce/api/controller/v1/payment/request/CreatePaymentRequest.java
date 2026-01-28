package com.dev.core.ecommerce.api.controller.v1.payment.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePaymentRequest(
        @NotBlank
        String orderKey
) {
}
