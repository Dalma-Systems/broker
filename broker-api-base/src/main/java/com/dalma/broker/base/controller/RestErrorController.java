package com.dalma.broker.base.controller;

import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import static com.dalma.broker.base.error.ErrorCode.UNKNOWN;

@Slf4j
@RestController
public class RestErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error"; // NOSONAR

    @RequestMapping(value = ERROR_PATH)
    private void error(HttpServletRequest request) {
        log.error("Handling error in rest controller");
        String msg = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));

        throw new RestResponseException(msg) {
            private static final long serialVersionUID = -4632592536703814073L;

            @Override
            public HttpStatus getHttpStatus() {
                var errorCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
                return httpStatus(errorCode);
            }

            @Override
            public String getErrorCode() {
                return UNKNOWN.code();
            }
        };
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    private HttpStatus httpStatus(Object statusCode) {
        if (statusCode != null) {
            if (statusCode instanceof Integer) {
                return HttpStatus.valueOf((Integer) statusCode);
            }

            try {
                return HttpStatus.valueOf(String.valueOf(statusCode));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
