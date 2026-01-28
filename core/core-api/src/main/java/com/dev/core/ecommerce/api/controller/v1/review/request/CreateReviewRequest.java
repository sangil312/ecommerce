package com.dev.core.ecommerce.api.controller.v1.review.request;

import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.ecommerce.service.review.request.ReviewContent;
import com.dev.core.ecommerce.service.review.request.ReviewTarget;
import com.dev.core.enums.review.ReviewTargetType;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public record CreateReviewRequest(
        ReviewTargetType targetType,
        Long targetId,
        BigDecimal rate,
        String content
) {
    public ReviewTarget toTarget() {
        return new ReviewTarget(targetType, targetId);
    }

    public ReviewContent toContent() {
        if (rate.compareTo(BigDecimal.ZERO) <= 0) throw new ApiException(ErrorType.INVALID_REQUEST);
        if (rate.compareTo(BigDecimal.valueOf(5.0)) > 0) throw new ApiException(ErrorType.INVALID_REQUEST);
        if (!StringUtils.hasText(content)) throw new ApiException(ErrorType.INVALID_REQUEST);
        return new ReviewContent(rate, content);
    }
}
