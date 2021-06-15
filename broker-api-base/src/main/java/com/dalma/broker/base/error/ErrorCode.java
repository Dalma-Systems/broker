package com.dalma.broker.base.error;

public enum ErrorCode implements BaseExceptionError {

    UNKNOWN("UNKNOWN_101", "Unknown error", null),
    INVALID_ARGUMENT("INVALID_ARGUMENT_101", "Invalid argument: {0}", "{0}"),
    MISSING_PARAMETER("BAD_REQUEST_101", "Missing request parameter: {0}", "{0}"),
    UNAUTHORIZED("UNAUTHORIZED_101", "Access Denied - needs authentication", "{0}"),
    FORBIDDEN("UNAUTHORIZED_102", "Access Denied - insufficient permissions", "{0}");

    String code;
    String message;
    String detail;

    ErrorCode(String code, String message, String detail) {
        this.code = code;
        this.message = message;
        this.detail = detail;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String code() {
        return this.code;
    }

}
