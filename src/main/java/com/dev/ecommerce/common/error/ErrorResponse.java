package com.dev.ecommerce.common.error;


public record ErrorResponse(
        int statusCode,
        String message
) {
    public static ErrorResponse of(ErrorType errorType) {
        return new ErrorResponse(
                errorType.getHttpStatus().value(),
                errorType.getMessage()
        );
    }
}
