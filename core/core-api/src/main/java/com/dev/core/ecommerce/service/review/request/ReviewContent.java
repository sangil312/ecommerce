package com.dev.core.ecommerce.service.review.request;

import java.math.BigDecimal;

public record ReviewContent(
        BigDecimal rate,
        String content
) {
}
