package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class CectificationNumMismatchException extends BusinessException {
    public CectificationNumMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
