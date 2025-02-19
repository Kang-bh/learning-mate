package org.study.learning_mate.global;

public enum ErrorCode {
    E400(400, "Bad Request"),
    E401(401, "Unauthorized"),
    E403(403, "Forbidden"),
    E404(404, "Not Found"),
    E405(405, "Method Not Allowed"),
    E409(409, "Conflict"),
    E500(500, "Internal Server Error"),
    E501(501, "Not Implemented"),
    E502(502, "Bad Gateway");

    private final int httpStatus;
    private final String message;

    ErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
