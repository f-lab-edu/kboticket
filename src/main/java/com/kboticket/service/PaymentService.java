package com.kboticket.service;

import com.kboticket.config.PaymentConfig;
import com.kboticket.config.payment.PaymentClient;
import com.kboticket.config.payment.PaymentProperties;
import com.kboticket.domain.Payment;
import com.kboticket.domain.User;
import com.kboticket.dto.payment.PaymentDto;
import com.kboticket.dto.payment.PaymentRequesteDto;
import com.kboticket.dto.payment.PaymentSuccessDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.PaymentRepository;
import com.kboticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentConfig paymentConfig;
    private final PaymentProperties paymentProperties;

    public Payment requestPayment(PaymentRequesteDto paymentRequesteDto, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_USER);
        });

        Payment payment = Payment.builder()
                .id(paymentRequesteDto.getOrderId())
                .user(user)
                .build();

        return paymentRepository.save(payment);
    }

    public  PaymentSuccessDto paymentSuccess(String paymentKey, String orderId, Long amount) {
        PaymentDto paymentDto = verifyPayment(orderId, amount);
        PaymentSuccessDto result = requestPaymentAccept(paymentKey, orderId, amount);

        paymentDto.setPaymentKey(paymentKey);
        paymentDto.setPaySuccessYn(true);
        paymentDto.getUser();

        return result;
    }

    // 결제 요청된 금액과 실제 결제된 금액이 같은지 확인
    public PaymentDto verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findById(orderId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.PAYMENT_NOT_FOUND);
        });

        if (!payment.getAmount().equals(amount)) {
            log.info("!payment.getAmount().equals(amount)" + !payment.getAmount().equals(amount));
            throw new KboTicketException(ErrorCode.PAYMENT_AMOUNT_EXP);
        }

        return PaymentDto.builder()
                .amount(payment.getAmount())
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getId())
                .build();
    }

    // 최종 결제 승인 요청을 보내기 위해 필요한 정보를 담아 post로 보내는 부분
    @Transactional
    public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();

        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);

        PaymentSuccessDto result = null;

        try {
            log.info("paymentConfig.getBaseUrl() + paymentKey ==> " + paymentConfig.getBaseUrl() + paymentKey);

            result = restTemplate.postForObject(paymentProperties.getBaseUrl() + paymentKey,
                    new HttpEntity<>(params, headers),
                    PaymentSuccessDto.class);

        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
            throw new KboTicketException(ErrorCode.ALREADY_APPROVED);
        }

        return result;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = new String(
                Base64.getEncoder().encode((paymentConfig.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8)));
        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public PaymentDto getOrderDetail(String orderId) {
        return null;
    }
}
