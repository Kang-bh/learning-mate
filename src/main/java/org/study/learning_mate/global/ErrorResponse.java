package org.study.learning_mate.global;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ErrorResponse extends RuntimeException {

    private String message;
    private ErrorCode code;


    private ErrorResponse(final ErrorType error) {
        this.code = error.getErrorCode();
        this.message = error.getMessage();
    }

    public ErrorResponse(final ErrorType error, final String message) {
        this.message = message;
        this.code = error.getErrorCode();
    }

    public static ErrorResponse of(final ErrorType code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ErrorType code, final String message) {
        return new ErrorResponse(code, message);
    }

}