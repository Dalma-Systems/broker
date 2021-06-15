package com.dalma.broker.service.error;

import com.dalma.broker.base.error.BaseExceptionError;

import static com.dalma.broker.service.error.BaseExceptionError.WAREHOUSE_ERROR_CODE;

public enum WarehouseExceptionError implements BaseExceptionError {
    NOT_FOUND(WAREHOUSE_ERROR_CODE.code() + "101", "Warehouse not found"),
    UPDATE_NOT_SUPPORTED(WAREHOUSE_ERROR_CODE.code() + "102", "Update not supported, unable to update materials"),
    ;

    private String code;
    private String message;

    private WarehouseExceptionError(String code, String message) {
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
