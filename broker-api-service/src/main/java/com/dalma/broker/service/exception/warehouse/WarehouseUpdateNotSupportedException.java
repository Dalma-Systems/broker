package com.dalma.broker.service.exception.warehouse;

import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static com.dalma.broker.service.error.WarehouseExceptionError.UPDATE_NOT_SUPPORTED;

@Slf4j
public class WarehouseUpdateNotSupportedException extends RestResponseException {

    private static final long serialVersionUID = 4450047851348743182L;

    public WarehouseUpdateNotSupportedException() {
        super(UPDATE_NOT_SUPPORTED.getMessage());
        log.error(UPDATE_NOT_SUPPORTED.getMessage());
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_ACCEPTABLE;
    }

    @Override
    public String getErrorCode() {
        return UPDATE_NOT_SUPPORTED.code();
    }
}
