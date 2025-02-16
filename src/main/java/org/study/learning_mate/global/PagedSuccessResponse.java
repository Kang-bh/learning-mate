package org.study.learning_mate.global;

import org.springframework.data.domain.Page;
import org.study.learning_mate.SuccessResponse;

import java.util.List;

public record PagedSuccessResponse<T>(
        int status,
        List<T> data,
        boolean last,
        int totalPages,
        long totalElements,
        int size,
        int page,
        boolean first
) {
    public static <T> PagedSuccessResponse<T> success(Page<T> page) {
        return new PagedSuccessResponse<>(
                200,
                page.getContent(),
                page.isLast(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.isFirst()
        );
    }

    public static <T> PagedSuccessResponse<T> success(int status, Page<T> page) {
        return new PagedSuccessResponse<>(
                status,
                page.getContent(),
                page.isLast(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.isFirst()
        );
    }
}


