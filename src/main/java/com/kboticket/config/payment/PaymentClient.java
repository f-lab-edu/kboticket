package com.kboticket.config.payment;

import com.kboticket.common.constants.KboConstant;
import com.kboticket.config.PaymentConfig;
import com.kboticket.dto.PaymentRequestInput;
import com.kboticket.dto.payment.PaymentCancelInput;
import com.kboticket.dto.payment.PaymentCancelResponse;
import com.kboticket.dto.payment.PaymentSuccessResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Component
public class PaymentClient {
    


    
    private final PaymentConfig paymentConfig;
    private RestClient restClient;
    
    public PaymentClient(PaymentConfig paymentConfig) {
        this.paymentConfig = paymentConfig;
        this.restClient = RestClient.builder()
                .requestFactory(createPaymenetRequestFactory())
                .requestInterceptor(new PaymentExceptionInterceptor())
                .defaultHeader(HttpHeaders.AUTHORIZATION, createPaymentAuthHeader(paymentConfig))
                .build();
    }

    private ClientHttpRequestFactory createPaymenetRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(KboConstant.CONNECT_TIMEOUT))
                .withReadTimeout(Duration.ofMillis(KboConstant.READ_TIMEOUT));

        return ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory.class, settings);
    }

    private String createPaymentAuthHeader(PaymentConfig paymentConfig) {
        byte[] encodedBytes = Base64.getEncoder()
                .encode((paymentConfig.getSecretKey() + KboConstant.BASIC_DLIIMITER).getBytes(StandardCharsets.UTF_8));

        return KboConstant.AUTH_HEADER_PREFIX + new String(encodedBytes);
    }

    public PaymentSuccessResponse requestPayment(PaymentRequestInput paymentRequestInput) {

        return restClient.method(HttpMethod.POST)
                .uri(paymentConfig.getBaseUrl() + paymentRequestInput.getPaymentKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequestInput)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new KboTicketException(ErrorCode.PAYMENT_CONFIRM_EXCEPTION);
                }))
                .body(PaymentSuccessResponse.class);
    }


    public PaymentCancelResponse cancelPayment(PaymentCancelInput input) {
        String paymentKey = input.getPaymentKey();

        return restClient.method(HttpMethod.POST)
                .uri(paymentConfig.getBaseUrl() + String.format(paymentConfig.getCancelEndpoint(), paymentKey))
                .contentType(MediaType.APPLICATION_JSON)
                .body(input)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new KboTicketException(ErrorCode.PAYMENT_CANCEL_EXCEPTION);
                }))
                .body(PaymentCancelResponse.class);
    }
}
