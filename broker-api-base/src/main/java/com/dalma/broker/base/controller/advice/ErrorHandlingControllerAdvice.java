package com.dalma.broker.base.controller.advice;

import com.dalma.broker.base.error.ErrorInfo;
import com.dalma.broker.base.error.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static com.dalma.broker.base.error.ErrorCode.INVALID_ARGUMENT;
import static com.dalma.broker.base.error.ErrorCode.MISSING_PARAMETER;

@Slf4j
@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    private static final String TRACE_ID = "traceId";

    @ExceptionHandler(RestResponseException.class)
    public ResponseEntity<ErrorInfo> handleRestResponseException(RestResponseException exception) {
        log.error("Handling exception in controller advice. Message: {}", exception.getMessage());

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setMessage(exception.getMessage());
        errorInfo.setTraceId(exception.getTraceId());
        errorInfo.setStatus(String.valueOf(exception.getHttpStatus().value()));
        errorInfo.setError(exception.getHttpStatus().getReasonPhrase());
        errorInfo.setCode(exception.getErrorCode());

        return ResponseEntity.status(exception.getHttpStatus()).body(errorInfo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("Handling exception in controller advice. Message:  {}", exception.getMessage());

        String errorMsg = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining("\n"));

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setMessage(errorMsg);
        errorInfo.setTraceId(MDC.get(TRACE_ID));
        errorInfo.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errorInfo.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorInfo.setCode(INVALID_ARGUMENT.code());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorInfo> handleConstraintViolationException(ConstraintViolationException exception) {
        log.error("Handling exception in controller advice. Message:  {}", exception.getMessage());

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setMessage(exception.getLocalizedMessage());
        errorInfo.setTraceId(MDC.get(TRACE_ID));
        errorInfo.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errorInfo.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorInfo> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        log.error("Handling exception in controller advice. Message: {}", exception.getMessage());

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setMessage(exception.getLocalizedMessage());
        errorInfo.setTraceId(MDC.get(TRACE_ID));
        errorInfo.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errorInfo.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorInfo.setCode(MISSING_PARAMETER.code());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }

}
