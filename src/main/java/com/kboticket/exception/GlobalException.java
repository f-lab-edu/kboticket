package com.kboticket.exception;

import com.kboticket.common.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(KboTicketException.class)
    public ResponseEntity<CommonResponse<?>> exceptionHandler(KboTicketException kboTicketException){

        Integer code = kboTicketException.getCode();
        String message = kboTicketException.getMessage();
        HttpStatus httpCode = kboTicketException.getHttpcode();

        CommonResponse<?> response = new CommonResponse<>(code, message, null);

        return ResponseEntity.status(httpCode).body(response);


    }
}
