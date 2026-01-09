package com.dev.core.ecommerce.controller.v1.payment.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePaymentRequest(
        @NotBlank
        String orderKey
) {
}
