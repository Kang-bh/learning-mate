package org.study.learning_mate;

public record SuccessResponse<T>(
        int status,
        T data
) {
    public static <T> SuccessResponse<T> success(int status, T data) {
        return new SuccessResponse<>(status, data);
    }

    public static <T> SuccessResponse<T> success(T data) {
        return new SuccessResponse<>(200, data);
    }
}
