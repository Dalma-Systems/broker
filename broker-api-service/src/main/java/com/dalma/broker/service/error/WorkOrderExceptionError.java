package com.dalma.broker.service.error;

import com.dalma.broker.base.error.BaseExceptionError;

import static com.dalma.broker.service.error.BaseExceptionError.WORKORDER_ERROR_CODE;

public enum WorkOrderExceptionError implements BaseExceptionError {
    UPDATE_NOT_SUPPORTED(WORKORDER_ERROR_CODE.code() + "101", "Update not supported, unable to update materials"),
    UPDATE_NOT_ALLOWED(WORKORDER_ERROR_CODE.code() + "102", "Update not allowed, current state: {0}"),
    NOT_FOUND(WORKORDER_ERROR_CODE.code() + "103", "Work order not found"),
    ;

    private String code;
    private String message;

    private WorkOrderExceptionError(String code, String message) {
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
