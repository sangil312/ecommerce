package com.dev.core.ecommerce.controller.v1.review;

import com.dev.core.ecommerce.RestDocsSupport;
import com.dev.core.ecommerce.support.response.Page;
import com.dev.core.ecommerce.controller.v1.review.request.CreateReviewRequest;
import com.dev.core.ecommerce.domain.review.Review;
import com.dev.core.ecommerce.service.review.ReviewService;
import com.dev.core.enums.review.ReviewTargetType;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

@ExtendWith(MockitoExtension.class)
class ReviewControllerRestDocsTest extends RestDocsSupport {
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = mock(ReviewService.class);
        mockMvc = mockController(new ReviewController(reviewService));
    }

    @Test
    @DisplayName("리뷰 생성 API")
    void create() {
        CreateReviewRequest request = new CreateReviewRequest(
                ReviewTargetType.PRODUCT,
                1L,
                BigDecimal.valueOf(4.5),
                "좋아요"
        );

        given().contentType(ContentType.JSON)
                .body(request)
                .post("/v1/reviews")
                .then()
                .status(HttpStatus.OK)
                .apply(document("reviews-create", requestPreprocessor(), responsePreprocessor(),
                        requestFields(
                                fieldWithPath("targetType").type(JsonFieldType.STRING).description("리뷰 대상 타입"),
                                fieldWithPath("targetId").type(JsonFieldType.NUMBER).description("리뷰 대상 ID"),
                                fieldWithPath("rate").type(JsonFieldType.NUMBER).description("평점"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용")
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 객체").optional(),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }

    @Test
    @DisplayName("리뷰 목록 조회 API")
    void findReviews() {
        Review review1 = mock(Review.class);
        when(review1.getId()).thenReturn(1L);
        when(review1.getTargetType()).thenReturn(ReviewTargetType.PRODUCT);
        when(review1.getTargetId()).thenReturn(1L);
        when(review1.getRate()).thenReturn(BigDecimal.valueOf(4.5));
        when(review1.getContent()).thenReturn("좋아요");

        when(reviewService.findReviews(eq(ReviewTargetType.PRODUCT), eq(1L), any(Pageable.class)))
                .thenReturn(Page.of(List.of(review1), false));

        given().contentType(ContentType.JSON)
                .queryParam("targetType", "PRODUCT")
                .queryParam("targetId", "1")
                .queryParam("page", "0")
                .queryParam("size", "20")
                .queryParam("sort", "createdAt,desc")
                .get("/v1/reviews")
                .then()
                .status(HttpStatus.OK)
                .apply(document("reviews-find", requestPreprocessor(), responsePreprocessor(),
                        queryParameters(
                                parameterWithName("targetType").description("리뷰 대상 타입"),
                                parameterWithName("targetId").description("리뷰 대상 ID"),
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 사이즈").optional(),
                                parameterWithName("sort").description("정렬 (createdAt,asc or desc)").optional()
                        ),
                        responseFields(
                                fieldWithPath("resultType").type(JsonFieldType.STRING).description("요청 결과"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 객체"),
                                fieldWithPath("data.contents").type(JsonFieldType.ARRAY).description("리뷰"),
                                fieldWithPath("data.contents[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                fieldWithPath("data.contents[].targetType").type(JsonFieldType.STRING)
                                        .description("리뷰 대상 타입"),
                                fieldWithPath("data.contents[].targetId").type(JsonFieldType.NUMBER)
                                        .description("리뷰 대상 ID"),
                                fieldWithPath("data.contents[].rate").type(JsonFieldType.NUMBER).description("평점"),
                                fieldWithPath("data.contents[].content").type(JsonFieldType.STRING)
                                        .description("리뷰 내용"),
                                fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }
}
