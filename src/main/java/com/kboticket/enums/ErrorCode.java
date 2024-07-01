package com.kboticket.enums;

public enum ErrorCode {

    DUPLICATE("B-001", "Duplicate Value"),
    NOT_FOUND("B-002", "Entity Not Found"),

    EMAIL_DUPLICATTE("U-001", "Duplicate Email Address"),
    PASSWORD_MISMATCH("U-002", "Password and Confirmpassword mismatch!!"),
    NOT_CHCKED_MANDATORY_TERMS("U-003", "Not Checked All Mandatory Terms!"),
    EMAIL_NOT_FOUND("U-004", "Email not found"),
    PHONE_DUPLICATE("U-005", "Duplicate Phone Number"),
    INVALID_VERIFICATION_KEY("U-006", "Invalid VerificationKey"),

    EMAIL_SEND_ERR("E-004", "Email Send Failed"),

    SMS_SEND_FAILED("S-001", "SMS Send Failed"),
    CERT_NUMBER_MISMATCH("S-002", "Certification Number is Mismatch!"),

    GENERATE_TEMP_PW_ERR("U-005", "Generate TempPassword Failed");

    private final String code;
    private final String message;

    ErrorCode(String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
