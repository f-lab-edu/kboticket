package com.kboticket.config.payment;

import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class PaymentExceptionInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            return execution.execute(request, body);
        } catch (IOException e) {
            throw new KboTicketException(ErrorCode.PAYMENT_TIMEOUT_EXCEPTION);
        } catch (Exception e) {
            throw new KboTicketException(ErrorCode.PAYMENT_CONFIRM_EXCEPTION);
        }
    }
}
