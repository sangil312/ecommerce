package com.dev.ecommerce.service.payment.response;


public record ApproveResult(
        Boolean isSuccess,
        ApproveFail fail,
        ApproveSuccess success
) {
}
