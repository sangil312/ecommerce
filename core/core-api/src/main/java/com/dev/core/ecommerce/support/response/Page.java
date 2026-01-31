package com.dev.core.ecommerce.support.response;

import java.util.List;

public record Page<T>(
        List<T> contents,
        Boolean hasNext
) {
    public static <T> Page<T> of(List<T> contents, Boolean hasNext) {
        return new Page<>(contents, hasNext);
    }

    public static <T> Page<T> empty() {
        return new Page<>(List.of(), false);
    }
}
