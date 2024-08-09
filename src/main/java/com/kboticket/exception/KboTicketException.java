package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Consumer;

@Getter
public class KboTicketException extends RuntimeException{

    private final Integer code;
    private final String message;
    private final HttpStatus httpcode;
    private final Map<String, Object> parameters;
    private final Consumer<String> logger;

    public KboTicketException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public KboTicketException(ErrorCode errorCode,Map<String, Object> parameters, Consumer<String> logger) {
        this.code = errorCode.code;
        this.message = errorCode.message;
        this.httpcode = errorCode.httpcode;
        this.parameters = parameters;
        this.logger = logger;
    }
}
