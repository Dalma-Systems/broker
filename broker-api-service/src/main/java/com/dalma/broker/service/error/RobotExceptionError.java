package com.dalma.broker.service.error;

import com.dalma.broker.base.error.BaseExceptionError;

import static com.dalma.broker.service.error.BaseExceptionError.ROBOT_ERROR_CODE;

public enum RobotExceptionError implements BaseExceptionError {
    MAC_ALREADY_EXISTS(ROBOT_ERROR_CODE.code() + "101", "Robot with mac address {0} already exists"),
    ;

    private String code;
    private String message;

    private RobotExceptionError(String code, String message) {
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
