package com.dev.ecommerce.controller.v1.response;


import com.dev.ecommerce.common.error.ErrorResponse;
import com.dev.ecommerce.common.error.ErrorType;

public record ApiResponse<T>(
        ResultType resultType,
        T data,
        ErrorResponse error
) {
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResultType.SUCCESS, null, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorType errorType) {
        return new ApiResponse<>(ResultType.ERROR, null, ErrorResponse.of(errorType));
    }
}
