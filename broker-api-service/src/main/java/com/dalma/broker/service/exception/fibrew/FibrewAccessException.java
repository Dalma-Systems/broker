package com.dalma.broker.service.exception.fibrew;

import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static com.dalma.broker.service.error.FibrewBridgeExceptionError.ACCESS_ERROR;

@Slf4j
public class FibrewAccessException extends RestResponseException {

	private static final long serialVersionUID = 2929686952168642637L;

	public FibrewAccessException(String error) {
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
