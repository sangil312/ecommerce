package com.dev.ecommerce.controller.v1.payment;

import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.controller.v1.payment.request.CreatePaymentRequest;
import com.dev.ecommerce.controller.v1.payment.response.CallbackSuccessResponse;
import com.dev.ecommerce.controller.v1.payment.response.CreatePaymentResponse;
import com.dev.ecommerce.controller.v1.response.ApiResponse;
import com.dev.ecommerce.service.payment.PaymentService;
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

    @PostMapping("/v1/payments")
    public ApiResponse<CreatePaymentResponse> create(
            User user,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        var paymentId = paymentService.create(user, request.orderKey());
        return ApiResponse.success(new CreatePaymentResponse(paymentId));
    }

    @PostMapping("/v1/payments/callback/success")
    public ApiResponse<Object> callbackSuccess(
            User user,
            @RequestParam String orderId,
            @RequestParam String paymentKey,
            @RequestParam BigDecimal amount
    ) {
        var paymentResult = paymentService.approve(user, orderId, paymentKey, amount);
        return paymentResult.isSuccess()
                ? ApiResponse.success()
                : ApiResponse.success(
                        new CallbackSuccessResponse(paymentResult.fail().code(), paymentResult.fail().message())
                );
    }
}
