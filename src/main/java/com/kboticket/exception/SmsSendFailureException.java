package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class SmsSendFailureException extends BusinessException {
    public SmsSendFailureException(ErrorCode errorCode) {
        super(errorCode);
    }
}
