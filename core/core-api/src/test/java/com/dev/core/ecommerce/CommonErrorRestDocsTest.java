package com.dev.core.ecommerce;

import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.controller.v1.product.ProductController;
import com.dev.core.ecommerce.service.product.ProductService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.dev.core.ecommerce.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@ExtendWith(MockitoExtension.class)
public class CommonErrorRestDocsTest extends RestDocsSupport {
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        mockMvc = mockController(new ProductController(productService));
    }

    @Test
    void commonErrorResponse() {
        when(productService.findProduct(anyLong()))
                .thenThrow(new ApiException(ErrorType.PRODUCT_NOT_FOUND));

        given().contentType(ContentType.JSON)
                .get("/v1/products/{productId}", 999L)
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
}
