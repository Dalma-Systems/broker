package com.dalma.broker.service.exception.workorder;

import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static com.dalma.broker.service.error.WorkOrderExceptionError.NOT_FOUND;

@Slf4j
public class WorkOrderNotFoundException extends RestResponseException {

    private static final long serialVersionUID = -4262313238282156528L;

    public WorkOrderNotFoundException() {
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
