package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class GenerateTempPwException extends BusinessException {
    public GenerateTempPwException(ErrorCode errorCode) {
        super(errorCode);
    }
}
