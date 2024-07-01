package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class EmailSendException extends BusinessException {
    public EmailSendException(ErrorCode errorCode) {

        super(errorCode);
    }
}
