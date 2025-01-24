package org.study.learning_mate.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, ErrorCode.E400, "Bad Request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCode.E403, "Forbidden"),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, ErrorCode.E400, "Bad Request"),



    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.E405, "Wrong HTTP method"),
    NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "Not Found"),
    CONFLICT(HttpStatus.CONFLICT, ErrorCode.E409, "Conflict"),

//    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "A1", "존재하지 않는 아티클입니다.");

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E501, "Error with Database"),
    INVALID_DATA_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E502,"DataIntegrityViolationException");

    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;
    private final String message;

    ErrorType(
            HttpStatus httpStatus,
            ErrorCode errorCode,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
}
