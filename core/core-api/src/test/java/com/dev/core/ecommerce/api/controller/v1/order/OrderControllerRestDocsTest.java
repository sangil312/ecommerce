package com.dev.core.ecommerce.api.controller.v1.order;

import com.dev.core.ecommerce.RestDocsSupport;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.api.controller.v1.order.request.CreateOrderFromCartRequest;
import com.dev.core.ecommerce.api.controller.v1.order.request.CreateOrderRequest;
import com.dev.core.ecommerce.service.order.dto.NewOrder;
import com.dev.core.ecommerce.service.order.OrderService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Set;
import java.util.UUID;

import static com.dev.core.ecommerce.RestDocsUtils.requestPreprocessor;
import static com.dev.core.ecommerce.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

@ExtendWith(MockitoExtension.class)
class OrderControllerRestDocsTest extends RestDocsSupport {
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        mockMvc = mockController(new OrderController(orderService));
    }

    @Test
    @DisplayName("주문 생성 API")
    void create() {
        String orderKey = UUID.randomUUID().toString();

        when(orderService.create(any(User.class), any(NewOrder.class))).thenReturn(orderKey);

        given().contentType(ContentType.JSON)
                .body(new CreateOrderRequest(1L, 2L))
                .post("/v1/orders")
                .then()
                .status(HttpStatus.OK)
                .apply(document("orders-create", requestPreprocessor(), responsePreprocessor(),
                        requestFields(
                                fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("주문 수량")
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 객체"),
                                fieldWithPath("data.orderKey").type(JsonFieldType.STRING).description("주문 KEY").attributes(),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }

    @Test
    @DisplayName("장바구니 주문 생성 API")
    void createFromCart() {
        String orderKey = UUID.randomUUID().toString();

        when(orderService.createFromCart(any(User.class), anyCollection())).thenReturn(orderKey);

        given().contentType(ContentType.JSON)
                .body(new CreateOrderFromCartRequest(Set.of(1L, 2L)))
                .post("/v1/cart-orders")
                .then()
                .status(HttpStatus.OK)
                .apply(document("orders-create-from-cart",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestFields(
                                fieldWithPath("cartItemIds").type(JsonFieldType.ARRAY).description("장바구니 상품 IDs")
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 객체"),
                                fieldWithPath("data.orderKey").type(JsonFieldType.STRING).description("주문 KEY"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }
}
