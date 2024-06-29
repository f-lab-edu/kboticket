package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class InvalidVerificationKeyException extends BusinessException {
    public InvalidVerificationKeyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
