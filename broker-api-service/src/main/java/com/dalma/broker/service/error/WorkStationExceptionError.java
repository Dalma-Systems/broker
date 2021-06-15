package com.dalma.broker.service.error;

import com.dalma.broker.base.error.BaseExceptionError;

import static com.dalma.broker.service.error.BaseExceptionError.WORKSTATION_ERROR_CODE;

public enum WorkStationExceptionError implements BaseExceptionError {
    NOT_FOUND(WORKSTATION_ERROR_CODE.code() + "101", "WorkStation not found"),
    ;

    private String code;
    private String message;

    private WorkStationExceptionError(String code, String message) {
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
