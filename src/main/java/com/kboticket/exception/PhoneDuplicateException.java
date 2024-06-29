package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class PhoneDuplicateException extends BusinessException {
    public PhoneDuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
