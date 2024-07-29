package com.kboticket.service;

import com.kboticket.config.PaymentConfig;
import com.kboticket.domain.Game;
import com.kboticket.domain.Payment;
import com.kboticket.domain.Seat;
import com.kboticket.domain.User;
import com.kboticket.dto.payment.*;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final UserService userService;
    private final GameService gameService;
    private final SeatService seatService;

    private final PaymentConfig paymentConfig;
    private final PaymentRepository paymentRepository;
    private final RedissonClient redissonClient;
    private static final String SEAT_LOCK = "seatLock:";

    // 결제 요청
    public Payment requestPayment(PaymentRequesteDto paymentRequesteDto, String email) {
        idReservedSeats(paymentRequesteDto.getSeatIds(), paymentRequesteDto.getGameId());

        User user = userService.getUser(email);
        Game game = gameService.getGame(paymentRequesteDto.getGameId());

        String orderId = createOrderId();

        List<Seat> seats = seatService.getSeatsById(paymentRequesteDto.getSeatIds() );
        // user
        // payment -> order -> user 하나에 너무 많이 걸리지 않게
        // 락 걸기
        // seat 추
        Payment payment = Payment.builder()
                .orderId(orderId)
                .game(game)
                .user(user)
                .amount(paymentRequesteDto.getAmount())
                .build();

        return paymentRepository.save(payment);
    }

    private void idReservedSeats(String seatIdsStr, Long gameId) {
        String[] seatsArr = seatIdsStr.split(",");

        Long[] seatIds = Arrays.stream(seatsArr)
                .map(id -> Long.parseLong(id))
                .toArray(Long[]::new);

        for (Long id : seatIds) {
            String key = SEAT_LOCK + gameId + id;
            RBucket<Object> bucket = redissonClient.getBucket(key);

            if (!bucket.isExists()) {
                throw new KboTicketException(ErrorCode.NOT_FOUND_RESERVATION);
            }
        }
    }

    // 결제 성공
    public  PaymentSuccessDto paymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = getPayment(orderId);
        // 결제 요청된 금액과 실제 결제된 금액이 같은지 확인
        isVerifyPayment(payment, amount);

        payment.setPaymentKey(paymentKey);

        paymentRepository.save(payment);

        // ticketService.createTicket(payment.getGame(), payment.getUser(), payment.getSeatIds());

        return requestPaymentAccept(paymentKey, orderId, amount);
    }

    // 결제 실패
    public PaymentFailDto paymentFail(String orderId) {
        Payment payment = getPayment(orderId);

        PaymentFailDto paymentFailDto = new PaymentFailDto();
        return paymentFailDto;
    }

    // 결제 취소
    public PaymentCancelDto paymentCancel(String paymentKey, String reason) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("reason", reason);

        return restTemplate.postForObject(paymentConfig.getBaseUrl() + paymentKey + "/cancel",
                new HttpEntity<>(params, headers),
                PaymentCancelDto.class);
    }

    // 결제 내역
    public List<PaymentHistoryDto> getPaymentHistory(String email) {
        User user = userService.getUser(email);

        return paymentRepository.findAllByUser(user);
    }

    // 결제 상세
    public PaymentDto getPaymentDetail(String email, String orderId) {
        return null;
    }

    // 결제 요청된 금액과 실제 결제된 금액이 같은지 확인
    public void isVerifyPayment(Payment payment, Long amount) {
        if (!payment.getAmount().equals(amount)) {
            log.info("!payment.getAmount().equals(amount)" + !payment.getAmount().equals(amount));
            throw new KboTicketException(ErrorCode.PAYMENT_AMOUNT_EXP);
        }
    }

    // 최종 결제 승인 요청을 보내기 위해 필요한 정보를 담아 post로 보냄
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

            result = restTemplate.postForObject(paymentConfig.getBaseUrl() + paymentKey,
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

    private String createOrderId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9);
    }

    private Payment getPayment(String orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.PAYMENT_NOT_FOUND);
        });
    }


}
