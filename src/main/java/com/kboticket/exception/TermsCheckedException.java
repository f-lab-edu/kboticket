package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class TermsCheckedException extends BusinessException {
    public TermsCheckedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
