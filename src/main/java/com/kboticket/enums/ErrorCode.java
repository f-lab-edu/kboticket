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
    AUTHENTICATION_ERR(20004, "invalid token", HttpStatus.BAD_REQUEST),



    /**
     * logic error code
     * 3xxxxx ~
     */
    NOT_FOUND_SEAT_INFO(30001, "Seat could not be found", HttpStatus.NOT_FOUND),
    NOT_FOUND_TICKET(3002, "Ticket could not be found", HttpStatus.NOT_FOUND),
    NOT_FOUND_USER(30003, "User could not be found", HttpStatus.NOT_FOUND),
    NOT_FOUND_GAME(30004, "Game could not be found", HttpStatus.NOT_FOUND),
    NOT_FOUND_TEAM(30005, "Team could not be found", HttpStatus.NOT_FOUND),
    NOT_FOUND_STADIUM(30006, "Stadium could not be found", HttpStatus.NOT_FOUND),
    NOT_FOUND_ORDER(30012, "Order could not be found", HttpStatus.NOT_FOUND),
    SEAT_ALREADY_RESERVED(30007, "Seat already reserved", HttpStatus.CONFLICT),
    INVALID_DATE_RANGE(30008, "Start date must be earlier than the end date", HttpStatus.BAD_REQUEST),
    SAME_TEAM_EXCEPTION(30009, "homeTeam and awayTeam must be different", HttpStatus.BAD_REQUEST),
    INVALID_HOMETEAM_STADIUM(30010, "The hometeam does not match the stadium", HttpStatus.BAD_REQUEST),
    DATE_OUT_OF_RANGE(30011, "Check the game schedule for one month before and after today", HttpStatus.BAD_REQUEST),
    EXCEED_TICKET_LIMIT(30013, "Attempting to reserve more tickets than allowed.", HttpStatus.BAD_REQUEST),
    TICKET_ALREADY_EXISTS(30014, "ticket already exists", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_CANCELED(30015, "order is already canceled", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(30016, "order is invalid", HttpStatus.BAD_REQUEST),
    USER_NOT_AUTHORIZED(30017, "user not authoritized", HttpStatus.BAD_REQUEST),
    TICKET_ALREADY_USED(30018, "ticket already used", HttpStatus.BAD_REQUEST),
    TICKET_ALREADY_CANCELLED(30019, "ticket already canceled", HttpStatus.BAD_REQUEST),
    INVALID_TICKET_STATUS(30020, "ticket invalid", HttpStatus.BAD_REQUEST),


    PAYMENT_FAILURE(30021, "Payment failed", HttpStatus.BAD_REQUEST),
    NOT_FOUND_SEAT_BLOCK(30022, "Seat block could not be found", HttpStatus.NOT_FOUND),
    INVALID_SEAT_LEVEL(30023, "Seat block could not be found", HttpStatus.BAD_REQUEST),
    EMPTY_SEATS_EXCEPTION(30024, "No seats were selected.Please select at least one seat.", HttpStatus.BAD_REQUEST),
    EXCEED_SEATS_LIMIT(30025, "The maximum number of seats : 4", HttpStatus.BAD_REQUEST),
    FAILED_RESERVATION(30026, "reservation be failed", HttpStatus.BAD_REQUEST),
    INVALID_START_DATE(30027, "Start date cannot be before the current date", HttpStatus.BAD_REQUEST),
    USER_ALREADY_OCCUPIED_SEAT(30028, "The user has already claimed another seat", HttpStatus.BAD_REQUEST),
    NOT_FOUND_RESERVATION(30029, "Reservation could not be found", HttpStatus.NOT_FOUND),
    FAILED_TRY_ROCK(30030, "Failed to acquire lock", HttpStatus.CONFLICT),


    PAYMENT_TIMEOUT_EXCEPTION(30031, "An tiemout error occurred during payment. ", HttpStatus.CONFLICT),
    PAYMENT_CONFIRM_EXCEPTION(30032, "An error occurred during confirm payment.", HttpStatus.CONFLICT),
    PAYMENT_CANCEL_EXCEPTION(30033, "An error occurred during cancel payment.", HttpStatus.CONFLICT),
    PAYMENT_NOT_FOUND(30034, "payment could not be found", HttpStatus.NOT_FOUND),
    PAYMENT_AMOUNT_EXP(30035, "invalid payment amount", HttpStatus.CONFLICT),
    ALREADY_APPROVED(30036, "already approved", HttpStatus.CONFLICT)
    ;

    public final int code;
    public final String message;
    public final HttpStatus httpcode;

    ErrorCode(int code, final String message, HttpStatus httpcode) {
        this.code = code;
        this.message = message;
        this.httpcode = httpcode;

    }
}
