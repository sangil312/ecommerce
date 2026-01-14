package com.dev.core.ecommerce.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, LogLevel.ERROR, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, LogLevel.INFO, "요청이 올바르지 않습니다."),

    // 상품
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, LogLevel.INFO, "상품이 존재하지 않습니다."),
    PRODUCT_MISMATCH_IN_ORDER(HttpStatus.BAD_REQUEST, LogLevel.INFO,"요청한 상품 정보와 일치하지 않습니다."),

    // 주문
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, LogLevel.INFO, "주문이 존재하지 않습니다."),

    // 결제
    PAYMENT_ALREADY_PAID(HttpStatus.BAD_REQUEST, LogLevel.INFO, "이미 결제가 완료된 주문입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, LogLevel.INFO, "결제 정보가 존재하지 않습니다."),
    PAYMENT_INVALID_STATUS(HttpStatus.BAD_REQUEST, LogLevel.INFO, "결제 상태가 유효하지 않습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, LogLevel.INFO, "결제 금액이 일치하지 않습니다."),
    PAYMENT_PG_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, LogLevel.ERROR, "결제 요청이 지연되고 있습니다. 잠시 후 다시 시도해주세요."),
    PAYMENT_APPROVE_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR, LogLevel.ERROR, "결제 승인 정보가 일치하지 않습니다. 고객센터에 문의해주세요."),
    ;

    private final HttpStatus httpStatus;
    private final LogLevel logLevel;
    private final String message;
}
