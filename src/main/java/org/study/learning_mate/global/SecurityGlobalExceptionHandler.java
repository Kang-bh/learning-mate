package org.study.learning_mate.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.study.learning_mate.global.ErrorResponse;
import org.study.learning_mate.global.ErrorType;

import java.io.IOException;

@Slf4j
public class SecurityGlobalExceptionHandler extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 필터 체인 실행
            filterChain.doFilter(request, response);
        } catch (Throwable ex) {
            // 발생한 예외를 ErrorResponse로 변환
            log.error("ex : " + ex);
            Throwable rootCause = getRootCause(ex);
            log.error("rootCause : " + rootCause);
            handleException(response, rootCause);
        }
    }

    private Throwable getRootCause(Throwable ex) {
        if (ex instanceof ServletException && ex.getCause() != null) {
            return ex.getCause();
        }
        return ex;
    }

    private void handleException(HttpServletResponse response, Throwable ex) throws IOException {
        ErrorResponse errorResponse;

        if (ex instanceof ErrorResponse) {
            // 이미 ErrorResponse로 던져진 경우 그대로 사용
            log.error("Error Response");
            errorResponse = (ErrorResponse) ex;
        } else {
            // 일반적인 예외는 UNKNOWN_ERROR로 Wrapping
            log.error("Unknown Exception");
            errorResponse = new ErrorResponse(ErrorType.SERVER_ERROR, ex.getMessage());
        }

        // HTTP 응답 설정
        response.setStatus(errorResponse.getCode().getHttpStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(new SimpleErrorResponse(
                errorResponse.getCode(),
                errorResponse.getMessage()
        ));

        response.getWriter().write(jsonResponse);
    }

    private static class SimpleErrorResponse {
        private final ErrorCode code;
        private final String message;

        public SimpleErrorResponse(ErrorCode code, String message) {
            this.code = code;
            this.message = message;
        }

        public ErrorCode getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
