package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class EmailDuplicateException extends BusinessException {

    public EmailDuplicateException (ErrorCode errorCode) {
        super(errorCode);
    }
}
