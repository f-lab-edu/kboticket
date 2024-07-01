package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.toString());
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
