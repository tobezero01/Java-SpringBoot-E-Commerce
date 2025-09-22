package com.eshop.client.dto.response;

import java.util.List;

public record PageResponse<T>(
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<T> content
) {
    public static <T> PageResponse<T> of(List<T> items, int page, int size, long totalItems, int totalPages) {
        return new PageResponse<>(size == 0 ? 1 : page, size == 0 ? items.size() : size, totalItems, totalPages, items);
    }
}
