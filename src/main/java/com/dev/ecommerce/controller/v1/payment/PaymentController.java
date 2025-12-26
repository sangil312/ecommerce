package com.dev.ecommerce.controller.v1.payment;

import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.controller.v1.payment.request.CreatePaymentRequest;
import com.dev.ecommerce.controller.v1.payment.response.CreatePaymentResponse;
import com.dev.ecommerce.controller.v1.response.ApiResponse;
import com.dev.ecommerce.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/api/payments")
    public ApiResponse<CreatePaymentResponse> create(
            User user,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        var paymentId = paymentService.create(user, request.orderKey());
        return ApiResponse.success(new CreatePaymentResponse(paymentId));
    }
}
