package com.dev.core.ecommerce.api.controller.v1.payment;

import com.dev.core.ecommerce.RestDocsSupport;
import com.dev.core.ecommerce.api.controller.v1.payment.usecase.PaymentUseCase;
import com.dev.core.ecommerce.service.payment.dto.PaymentConfirmFail;
import com.dev.core.ecommerce.service.payment.dto.PaymentConfirmResult;
import com.dev.core.ecommerce.service.payment.dto.PaymentConfirmSuccess;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.api.controller.v1.payment.request.CreatePaymentRequest;
import com.dev.core.ecommerce.service.payment.PaymentService;
import com.dev.core.enums.payment.PaymentMethod;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.dev.core.ecommerce.RestDocsUtils.requestPreprocessor;
import static com.dev.core.ecommerce.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

@ExtendWith(MockitoExtension.class)
class PaymentControllerRestDocsTest extends RestDocsSupport {
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        mockMvc = mockController(new PaymentController(paymentService, mock(PaymentUseCase.class)));
    }

    @Test
    @DisplayName("결제 생성 API")
    void create() {
        given().contentType(ContentType.JSON)
                .body(new CreatePaymentRequest("order-123"))
                .post("/v1/payments")
                .then()
                .status(HttpStatus.OK)
                .apply(document("payments-create",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestFields(
                                fieldWithPath("orderKey").type(JsonFieldType.STRING).description("Order key")
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("Result type"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("Response data"),
                                fieldWithPath("data.paymentId").type(JsonFieldType.NUMBER).description("Payment id"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("Error response").optional()
                        )
                ));
    }

    @Test
    @DisplayName("결제 요청 성공 콜백 API > 결제 승인 성공")
    void callbackSuccess() {
        PaymentConfirmResult paymentConfirmResult = new PaymentConfirmResult(
                true,
                new PaymentConfirmSuccess(
                        "pay-1",
                        "order-123",
                        PaymentMethod.CARD,
                        BigDecimal.valueOf(1_000),
                        LocalDateTime.now()
                ),
                null
        );

        when(paymentService.success(any(User.class), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(paymentConfirmResult);

        given().contentType(ContentType.URLENC)
                .formParam("orderId", "order-123")
                .formParam("paymentKey", "pay-1")
                .formParam("amount", "1000")
                .post("/v1/payments/callback/success")
                .then()
                .status(HttpStatus.OK)
                .apply(document("payments-callback-success", requestPreprocessor(), responsePreprocessor(),
                        formParameters(
                                parameterWithName("orderId").description("주문 KEY"),
                                parameterWithName("paymentKey").description("PG 결제 KEY"),
                                parameterWithName("amount").description("결제 금액")
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 객체").optional(),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }

    @Test
    @DisplayName("결제 요청 성공 콜백 API > 결제 승인 실패")
    void callbackSuccessFail() {
        PaymentConfirmResult paymentConfirmResult = new PaymentConfirmResult(
                false,
                null,
                new PaymentConfirmFail("ERROR_CODE", "한도 초과")
        );

        when(paymentService.success(any(User.class), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(paymentConfirmResult);

        given().contentType(ContentType.URLENC)
                .formParam("orderId", "order-123")
                .formParam("paymentKey", "pay-1")
                .formParam("amount", "1000")
                .post("/v1/payments/callback/success")
                .then()
                .status(HttpStatus.OK)
                .apply(document("payments-callback-success-fail", requestPreprocessor(), responsePreprocessor(),
                        formParameters(
                                parameterWithName("orderId").description("주문 KEY"),
                                parameterWithName("paymentKey").description("PG 결제 KEY"),
                                parameterWithName("amount").description("결제 금액")
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 객체"),
                                fieldWithPath("data.code").type(JsonFieldType.STRING).description("결제 실패 코드"),
                                fieldWithPath("data.message").type(JsonFieldType.STRING).description("결제 실패 메세지"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }

    @Test
    @DisplayName("결제 요청 실패 콜백 API")
    void callbackFail() {
        given().contentType(ContentType.URLENC)
                .formParam("orderId", "order-123")
                .formParam("code", "FAIL")
                .formParam("message", "failed")
                .post("/v1/payments/callback/fail")
                .then()
                .status(HttpStatus.OK)
                .apply(document("payments-callback-fail",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        formParameters(
                                parameterWithName("orderId").description("주문 KEY"),
                                parameterWithName("code").description("결제 요청 실패 코드"),
                                parameterWithName("message").description("결제 요청 실패 메세지")
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 객체").optional(),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }
}
