package org.study.learning_mate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Map;

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

    public static <T> ResponseEntity<SuccessResponse<T>> successWithHeaders(int status, T data, Map<String, String> headersMap) {
        HttpHeaders headers = new HttpHeaders();
        headersMap.forEach(headers::add);
        return new ResponseEntity<>(success(status, data), headers, status);
    }
}
