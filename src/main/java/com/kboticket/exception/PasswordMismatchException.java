package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
