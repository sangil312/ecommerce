package com.dev.ecommerce.controller.response;

import java.util.List;

public record PageResponse<T>(
        List<T> contents,
        Boolean hasNext
) {
    public static <T> PageResponse<T> of(List<T> contents, Boolean hasNext) {
        return new PageResponse<>(contents, hasNext);
    }
}
