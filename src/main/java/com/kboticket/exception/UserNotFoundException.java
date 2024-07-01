package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
