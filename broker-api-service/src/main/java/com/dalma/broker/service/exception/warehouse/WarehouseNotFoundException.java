package com.dalma.broker.service.exception.warehouse;

import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static com.dalma.broker.service.error.WarehouseExceptionError.NOT_FOUND;

@Slf4j
public class WarehouseNotFoundException extends RestResponseException {

    private static final long serialVersionUID = 3544621144649085589L;

    public WarehouseNotFoundException() {
        super(NOT_FOUND.getMessage());
        log.error(NOT_FOUND.getMessage());
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return NOT_FOUND.code();
    }
}
