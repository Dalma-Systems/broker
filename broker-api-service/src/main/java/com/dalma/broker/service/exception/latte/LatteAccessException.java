package com.dalma.broker.service.exception.latte;

import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static com.dalma.broker.service.error.LatteBridgeExceptionError.ACCESS_ERROR;

@Slf4j
public class LatteAccessException extends RestResponseException {

    private static final long serialVersionUID = 8094653957064603834L;

    public LatteAccessException(String error) {
        super(ACCESS_ERROR.message(error));
        log.error(ACCESS_ERROR.message(error));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getErrorCode() {
        return ACCESS_ERROR.code();
    }
}
