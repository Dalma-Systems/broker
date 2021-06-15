package com.dalma.broker.service.error;

import com.dalma.broker.base.error.BaseExceptionError;

import static com.dalma.broker.service.error.BaseExceptionError.LATTE_ERROR_CODE;

public enum LatteBridgeExceptionError implements BaseExceptionError {
    ACCESS_ERROR(LATTE_ERROR_CODE.code() + "101", "Error accessing latte bridge: {0}"),
    ;

    private String code;
    private String message;

    private LatteBridgeExceptionError(String code, String message) {
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
