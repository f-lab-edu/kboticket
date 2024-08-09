package com.kboticket.exception;

import com.kboticket.common.CommonResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.function.Consumer;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(KboTicketException.class)
    @Order(2)
    public ResponseEntity<CommonResponse<?>> exceptionHandler(KboTicketException kboTicketException){
        Integer code = kboTicketException.getCode();
        String message = kboTicketException.getMessage();
        HttpStatus httpCode = kboTicketException.getHttpcode();
        Map<String, Object> parameters = kboTicketException.getParameters();
        Consumer<String> logger = kboTicketException.getLogger();

        if (kboTicketException.getLogger() != null) {
            kboTicketException.getLogger().accept(kboTicketException.getMessage());
        }

        //CommonResponse<?> response = new CommonResponse<>(code, message);
        CommonResponse<?> response = new CommonResponse<>(code, message, parameters, logger);

        return ResponseEntity.status(httpCode).body(response);
    }

    @ExceptionHandler(Exception.class)
    @Order(1)
    public ResponseEntity<CommonResponse<?>> exceptionHandler(Exception exception) {
        CommonResponse<?> response = new CommonResponse<>(500, "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}