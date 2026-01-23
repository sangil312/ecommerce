package com.dev.core.ecommerce.controller.v1.product;

import com.dev.core.ecommerce.RestDocsSupport;
import com.dev.core.ecommerce.common.response.Page;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.service.product.ProductService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;

import java.math.BigDecimal;
import java.util.List;

import static com.dev.core.ecommerce.RestDocsUtils.requestPreprocessor;
import static com.dev.core.ecommerce.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

@ExtendWith(MockitoExtension.class)
class ProductControllerRestDocsTest extends RestDocsSupport {
    private ProductService productService;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        mockMvc = mockController(new ProductController(productService));
        testProduct = mock(Product.class);
        when(testProduct.getId()).thenReturn(1L);
        when(testProduct.getName()).thenReturn("상품1");
        when(testProduct.getDescription()).thenReturn("상품1 설명");
        when(testProduct.getPrice()).thenReturn(BigDecimal.valueOf(1_000));
    }

    @Test
    @DisplayName("상품 목록 조회")
    void findProducts() {
        Product product2 = mock(Product.class);
        when(product2.getId()).thenReturn(2L);
        when(product2.getName()).thenReturn("상품2");
        when(product2.getDescription()).thenReturn("상품2 설명");
        when(product2.getPrice()).thenReturn(BigDecimal.valueOf(2_000));

        when(productService.findProducts(eq(1L), any(Pageable.class)))
                .thenReturn(Page.of(List.of(testProduct, product2), false));

        given().contentType(ContentType.JSON)
                .queryParam("categoryId", "1")
                .queryParam("page", "0")
                .queryParam("size", "20")
                .queryParam("sort", "price,desc")
                .get("/v1/products")
                .then()
                .status(HttpStatus.OK)
                .apply(document("products-find", requestPreprocessor(), responsePreprocessor(),
                        queryParameters(
                                parameterWithName("categoryId").description("카테고리 ID"),
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 사이즈").optional(),
                                parameterWithName("sort").description("정렬").optional()
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 객체"),
                                fieldWithPath("data.contents").type(JsonFieldType.ARRAY).description("상품"),
                                fieldWithPath("data.contents[].productId").type(JsonFieldType.NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data.contents[].name").type(JsonFieldType.STRING).description("상품명"),
                                fieldWithPath("data.contents[].description").type(JsonFieldType.STRING)
                                        .description("상품 설명"),
                                fieldWithPath("data.contents[].price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }

    @Test
    @DisplayName("상품 상세 조회")
    void findProductDetail() {
        when(productService.findProduct(1L)).thenReturn(testProduct);

        given().contentType(ContentType.JSON)
                .get("/v1/products/{productId}", 1L)
                .then()
                .status(HttpStatus.OK)
                .apply(document("products-find-detail", requestPreprocessor(), responsePreprocessor(),
                        pathParameters(
                                parameterWithName("productId").description("상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 객체"),
                                fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("상품명"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }
}
