package com.dev.ecommerce.controller.response;


public record ApiResponse<T>(
        ResultType resultType,
        T data,
        ErrorResponse error
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }
}
