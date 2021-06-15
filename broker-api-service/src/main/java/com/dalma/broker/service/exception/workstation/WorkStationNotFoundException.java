package com.dalma.broker.service.exception.workstation;

import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static com.dalma.broker.service.error.WorkStationExceptionError.NOT_FOUND;

@Slf4j
public class WorkStationNotFoundException extends RestResponseException {

    private static final long serialVersionUID = -3612427330672400714L;

    public WorkStationNotFoundException() {
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
