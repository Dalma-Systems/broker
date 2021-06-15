package com.dalma.broker.base.error;

import java.text.MessageFormat;

public interface BaseExceptionError {

    default String message() {
        return getMessage();
    }

    default String message(String detail) {
        return MessageFormat.format(getMessage(), detail);
    }

    default String message(Long id) {
        return MessageFormat.format(getMessage(), id);
    }

    String getMessage();

    String code();

}
