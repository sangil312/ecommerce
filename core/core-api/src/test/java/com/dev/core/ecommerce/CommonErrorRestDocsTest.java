package com.dev.core.ecommerce;

import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dev.core.ecommerce.RestDocsUtils.responsePreprocessor;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@ExtendWith(MockitoExtension.class)
public class CommonErrorRestDocsTest extends RestDocsSupport {

    @BeforeEach
    void setUp() {
        mockMvc = mockController(new ErrorDocController());
    }

    @Test
    void commonErrorResponse() {
        given().contentType(ContentType.JSON)
                .get("/docs/errors")
                .then()
                .apply(document("common-error", responsePreprocessor(),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 객체").optional(),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러 응답 객체"),
                                fieldWithPath("error.statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메세지")
                        )
                ));
    }

    @RestController
    static class ErrorDocController {
        @GetMapping("/docs/errors")
        public void error() {
            throw new ApiException(ErrorType.PRODUCT_NOT_FOUND);
        }
    }
}
