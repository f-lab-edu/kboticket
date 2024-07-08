package com.kboticket.exception;

import com.kboticket.enums.ErrorCode;

public class TermsNotFoundException extends BusinessException {
    public TermsNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
