package com.dalma.broker.service.error;

public enum BaseExceptionError {
    WAREHOUSE_ERROR_CODE("WAREHOUSE"), 
    WORKORDER_ERROR_CODE("WORKORDER"), 
    WORKSTATION_ERROR_CODE("WORKSTATION"),
    ROBOT_ERROR_CODE("ROBOT"),
    LATTE_ERROR_CODE("LATTE"),
    FIBREW_ERROR_CODE("FIBREW"),
    ;

    public static final String DELIMITER = "_";

    String code;

    BaseExceptionError(String code) {
        this.code = code;
    }

    public String code() {
        return code + DELIMITER;
    }
}
