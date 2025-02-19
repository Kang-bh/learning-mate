package org.study.learning_mate.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.study.learning_mate.global.ErrorResponse;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorResponse.class)
    public void handleCustomError(ErrorResponse error) throws ErrorResponse {
        log.error("nice error");
        throw error;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) throws ErrorResponse {
        throw new ErrorResponse(
                ErrorType.SERVER_ERROR,
                ex.getMessage()
        );
    }
}
