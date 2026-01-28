package com.dev.core.ecommerce.api.controller.v1.response;

import java.util.List;

public record PageResponse<T>(
        List<T> contents,
        Boolean hasNext
) {
    public static <T> PageResponse<T> of(List<T> contents, Boolean hasNext) {
        return new PageResponse<>(contents, hasNext);
    }
}
