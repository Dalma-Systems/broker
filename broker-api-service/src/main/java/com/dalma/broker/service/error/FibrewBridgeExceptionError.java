package com.dalma.broker.service.error;

import com.dalma.broker.base.error.BaseExceptionError;

import static com.dalma.broker.service.error.BaseExceptionError.FIBREW_ERROR_CODE;

public enum FibrewBridgeExceptionError implements BaseExceptionError {
    ACCESS_ERROR(FIBREW_ERROR_CODE.code() + "101", "Error accessing fibrew bridge: {0}"),
    ;

    private String code;
    private String message;

    private FibrewBridgeExceptionError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String code() {
        return code;
    }
}
