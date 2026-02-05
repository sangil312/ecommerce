package com.dev.core.ecommerce.api.controller.v1.payment;

import com.dev.core.ecommerce.api.controller.v1.payment.usecase.PaymentUseCase;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.api.controller.v1.payment.request.CreatePaymentRequest;
import com.dev.core.ecommerce.api.controller.v1.payment.response.PaymentApproveResponse;
import com.dev.core.ecommerce.api.controller.v1.payment.response.CreatePaymentResponse;
import com.dev.core.ecommerce.api.controller.v1.response.ApiResponse;
import com.dev.core.ecommerce.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentUseCase paymentUseCase;

    @PostMapping("/v1/payments")
    public ApiResponse<CreatePaymentResponse> create(
            User user,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        var paymentId = paymentUseCase.createPayment(user, request.orderKey());
        return ApiResponse.success(new CreatePaymentResponse(paymentId));
    }

    @PostMapping("/v1/payments/callback/success")
    public ApiResponse<PaymentApproveResponse> callbackSuccess(
            User user,
            @RequestParam String orderId,
            @RequestParam String paymentKey,
            @RequestParam BigDecimal amount
    ) {
        var paymentResult = paymentService.success(user, orderId, paymentKey, amount);
        return ApiResponse.success(PaymentApproveResponse.of(paymentResult));
    }

    @PostMapping("/v1/payments/callback/fail")
    public ApiResponse<Object> callbackFail(
            User user,
            @RequestParam String orderId,
            @RequestParam String code,
            @RequestParam String message
    ) {
        paymentUseCase.failPayment(user, orderId, code, message);
        return ApiResponse.success();
    }
}
