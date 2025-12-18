package com.dev.ecommerce.common.error;

public record ErrorResponse(
        String error,
        int statusCode,
        String message
) {
    public static ErrorResponse of(ErrorType errorType) {
        return new ErrorResponse(
                errorType.getHttpStatus().getReasonPhrase(),
                errorType.getHttpStatus().value(),
                errorType.getMessage()
        );
    }
}
