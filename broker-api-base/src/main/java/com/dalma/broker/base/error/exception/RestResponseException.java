package com.dalma.broker.base.error.exception;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

public abstract class RestResponseException extends RuntimeException {

    private static final long serialVersionUID = 9120765498547869491L;

    private final String traceId;

    /**
     * Construct a with the specified detail message.
     *
     * @param msg the detail message
     */
    public RestResponseException(String msg) {
        super(msg);
        traceId = MDC.get("traceId");
    }

    public String getTraceId() {
        return traceId;
    }

    public abstract HttpStatus getHttpStatus();

    public abstract String getErrorCode();

}
