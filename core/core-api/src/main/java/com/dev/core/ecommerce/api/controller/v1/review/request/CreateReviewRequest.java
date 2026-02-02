package com.dev.core.ecommerce.api.controller.v1.review.request;

import com.dev.core.ecommerce.service.review.dto.ReviewContent;
import com.dev.core.ecommerce.service.review.dto.ReviewTarget;
import com.dev.core.enums.review.ReviewTargetType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateReviewRequest(
        @NotNull
        ReviewTargetType targetType,

        @NotNull
        Long targetId,

        @DecimalMin("0.0") @DecimalMax("5.0")
        @NotNull
        BigDecimal rate,

        @NotBlank
        String content,

        List<Long> imageIds
) {
    public ReviewTarget toTarget() {
        return ReviewTarget.of(targetType, targetId);
    }

    public ReviewContent toContent() {
        return ReviewContent.of(rate, content);
    }
}
