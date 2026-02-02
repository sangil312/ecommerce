package com.dev.core.ecommerce.api.controller.v1.review;

import com.dev.core.ecommerce.RestDocsSupport;
import com.dev.core.ecommerce.api.controller.v1.review.usecase.ReviewUseCase;
import com.dev.core.ecommerce.api.controller.v1.review.request.CreateReviewRequest;
import com.dev.core.ecommerce.api.controller.v1.review.response.ReviewImageResponse;
import com.dev.core.ecommerce.api.controller.v1.review.response.ReviewListResponse;
import com.dev.core.ecommerce.api.controller.v1.review.response.ReviewResponse2;
import com.dev.core.ecommerce.service.review.ReviewService;
import com.dev.core.ecommerce.service.review.dto.RateSummary;
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
import java.time.LocalDateTime;
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
    private ReviewUseCase reviewUseCase;

    @BeforeEach
    void setUp() {
        reviewUseCase = mock(ReviewUseCase.class);
        mockMvc = mockController(new ReviewController(mock(ReviewService.class), reviewUseCase));
    }

    @Test
    @DisplayName("리뷰 생성 API")
    void create() {
        CreateReviewRequest request = new CreateReviewRequest(
                ReviewTargetType.PRODUCT,
                1L,
                BigDecimal.valueOf(4.5),
                "좋아요",
                List.of(1L, 2L)
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
                                fieldWithPath("rate").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("imageIds").type(JsonFieldType.ARRAY).description("리뷰 이미지 ID 목록")
                                        .optional()
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
        ReviewResponse2 review = new ReviewResponse2(
                1L,
                ReviewTargetType.PRODUCT,
                1L,
                "상품1",
                BigDecimal.valueOf(4.5),
                "좋아요",
                LocalDateTime.of(2024, 1, 1, 0, 0),
                List.of(new ReviewImageResponse(1L, "http://image.test/1"))
        );
        ReviewListResponse reviewListResponse = ReviewListResponse.of(
                "상품1",
                new RateSummary(10L, BigDecimal.valueOf(4.3)),
                List.of(review),
                false
        );
        when(reviewUseCase.findReviews(eq(ReviewTargetType.PRODUCT), eq(1L), any(Pageable.class)))
                .thenReturn(reviewListResponse);

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
                                fieldWithPath("data.productName").type(JsonFieldType.STRING).description("상품 명"),
                                fieldWithPath("data.rateSummary").type(JsonFieldType.OBJECT).description("리뷰 전체 요약"),
                                fieldWithPath("data.rateSummary.count").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                fieldWithPath("data.rateSummary.rate").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                                fieldWithPath("data.contents").type(JsonFieldType.ARRAY).description("리뷰"),
                                fieldWithPath("data.contents[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                fieldWithPath("data.contents[].targetType").type(JsonFieldType.STRING)
                                        .description("리뷰 대상 타입"),
                                fieldWithPath("data.contents[].targetId").type(JsonFieldType.NUMBER)
                                        .description("리뷰 대상 ID"),
                                fieldWithPath("data.contents[].targetName").type(JsonFieldType.STRING).description("리뷰 상품 명"),
                                fieldWithPath("data.contents[].rate").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                                fieldWithPath("data.contents[].content").type(JsonFieldType.STRING)
                                        .description("리뷰 내용"),
                                fieldWithPath("data.contents[].createdAt").type(JsonFieldType.STRING).description("리뷰 작성일"),
                                fieldWithPath("data.contents[].images").type(JsonFieldType.ARRAY).description("리뷰 이미지"),
                                fieldWithPath("data.contents[].images[].id").type(JsonFieldType.NUMBER).description("이미지 ID"),
                                fieldWithPath("data.contents[].images[].imageUrl").type(JsonFieldType.STRING).description("이미지 URL"),
                                fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재여부"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 응답 객체").optional()
                        )
                ));
    }
}
