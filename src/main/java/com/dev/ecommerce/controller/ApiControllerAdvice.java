package com.dev.ecommerce.controller;

import com.dev.ecommerce.common.error.ApiException;
import com.dev.ecommerce.common.error.ErrorType;
import com.dev.ecommerce.controller.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException e) {
        ErrorType errorType = e.getErrorType();

        switch (errorType.getLogLevel()) {
            case ERROR -> log.error("ApiException: {}", e.getMessage(), e);
            case WARN -> log.warn("ApiException: {}", e.getMessage(), e);
            default -> log.info("ApiException: {}", e.getMessage(), e);
        }
        return ResponseEntity.status(errorType.getHttpStatus()).body(ApiResponse.error(errorType));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(ApiResponse.error(ErrorType.DEFAULT_ERROR));
    }

    @ExceptionHandler({
            ServletRequestBindingException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleRequestParam(Exception e) {
        log.info("{}: {}", e.getClass(), e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorType.INVALID_REQUEST));
    }
}
