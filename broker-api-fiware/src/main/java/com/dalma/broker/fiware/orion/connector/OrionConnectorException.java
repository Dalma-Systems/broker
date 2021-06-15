package com.dalma.broker.fiware.orion.connector;

import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class OrionConnectorException extends RestResponseException {

    private static final long serialVersionUID = -7152578409618752018L;

    public OrionConnectorException(String msg, Throwable e) {
        super(msg);
        if (e != null) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return HttpStatus.BAD_REQUEST.toString();
    }
}
