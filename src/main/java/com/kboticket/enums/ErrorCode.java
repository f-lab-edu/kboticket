package com.kboticket.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    /**
     * user error code
     * 1xxxxx ~
     */
    PHONE_DUPLICATE(10001, "Duplicate Phone number", HttpStatus.CONFLICT),
    FAILED_SEND_VERIFICATION_CODE(10002, "Failed to send verification code", HttpStatus.CONFLICT),
    INVALID_VERIFICATION_CODE(10003, "Invalid verification code", HttpStatus.NOT_FOUND),

    EMAIL_DUPLICATTE(10004, "Email already exists", HttpStatus.CONFLICT),
    INVALID_EMAIL_FORMAT(10005, "Invalid Email format", HttpStatus.CONFLICT),
    INVALID_PASSWORD_FORMAT(10006, "Invalid Password format", HttpStatus.CONFLICT),
    TERM_NOT_FOUND_EXCEPTION(10007, "Terms not found", HttpStatus.NOT_FOUND),
    NOT_CHECKED_MANDATORY_TERMS(10008, "Required Terms not accepted", HttpStatus.BAD_REQUEST),

    PHONE_NOT_FOUND(10009, "Phone not found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND(10010, "Email not found", HttpStatus.NOT_FOUND),
    PASSWORD_NOT_FOUND(10011, "Password not found", HttpStatus.NOT_FOUND),
    PASSWORD_MISMATCH(10012, "Password and confirmpassword mismatch", HttpStatus.BAD_REQUEST),
    GENERATE_TEMP_PW_ERR(10013, "Generate temp Password Failed", HttpStatus.CONFLICT),



    /**
     * sms error code
     * 2xxxxx ~
     */
    SMS_SEND_FAILED(20001, "SMS send failed", HttpStatus.BAD_REQUEST),
    CERT_NUMBER_MISMATCH(20002, "Certification Number is Mismatch!", HttpStatus.NOT_FOUND),
    AUTHENTICATION_ERR(20004, "invalid token", HttpStatus.BAD_REQUEST);



    public final int code;
    public final String message;
    public final HttpStatus httpcode;

    ErrorCode(int code, final String message, HttpStatus httpcode) {
        this.code = code;
        this.message = message;
        this.httpcode = httpcode;

    }
}
