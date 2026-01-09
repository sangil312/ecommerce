package com.dev.core.ecommerce.common.response;

import java.util.List;

public record Page<T>(
        List<T> contents,
        Boolean hasNext
) {
    public static <T> Page<T> of(List<T> contents, Boolean hasNext) {
        return new Page<>(contents, hasNext);
    }
}
