package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class EmailNotFoundException extends BusinessException {
    public EmailNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
