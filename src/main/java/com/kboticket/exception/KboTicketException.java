package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KboTicketException extends RuntimeException{

    private final Integer code;
    private final String message;
    private final HttpStatus httpcode;

    public KboTicketException(ErrorCode errorCode) {
        this.code = errorCode.code;
        this.message = errorCode.message;
        this.httpcode = errorCode.httpcode;
    }
}
