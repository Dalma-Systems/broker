package com.dalma.broker.service.exception.robot;

import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static com.dalma.broker.service.error.RobotExceptionError.MAC_ALREADY_EXISTS;

@Slf4j
public class RobotAlreadyExistsException extends RestResponseException {

	public static final String ERROR_FIELD = "description";
	public static final String ERROR_MESSAGE = "Already Exists";
	private static final long serialVersionUID = -2130869021044132610L;

	public RobotAlreadyExistsException(String mac) {
        super(MAC_ALREADY_EXISTS.message(mac));
        log.error(MAC_ALREADY_EXISTS.message(mac));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return MAC_ALREADY_EXISTS.code();
    }
}
