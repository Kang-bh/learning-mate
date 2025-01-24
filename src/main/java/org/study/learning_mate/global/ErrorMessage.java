package org.study.learning_mate.global;



public class ErrorMessage {
    private final String code;
    private final String message;
    private final Object data;


    public ErrorMessage(ErrorType errorType) {
        this.code = errorType.getErrorCode().name();
        this.message = errorType.getMessage();
        this.data = null;
    }

    public ErrorMessage(ErrorType errorType, Object data) {
        this.code = errorType.getErrorCode().name();
        this.message = errorType.getMessage();
        this.data = data;
    }
}
