package com.kboticket.service;

import com.kboticket.config.PaymentConfig;
import com.kboticket.config.payment.PaymentClient;
import com.kboticket.domain.Order;
import com.kboticket.domain.Payment;
import com.kboticket.domain.User;
import com.kboticket.dto.PaymentRequestInput;
import com.kboticket.dto.ReservedSeatInfo;
import com.kboticket.dto.payment.*;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.PaymentStatus;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
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
    private final OrderService orderService;
    private final TicketService ticketService;

    private final PaymentConfig paymentConfig;
    private final PaymentRepository paymentRepository;
    private final RedissonClient redissonClient;
    private static final String SEAT_LOCK = "seatLock:";

    // 결제 요청
    public void requestPayment(PaymentRequestDto paymentRequestDto, String loginId) {
        Long gameId = paymentRequestDto.getGameId();
        Set<Long> seatIds = paymentRequestDto.getSeatIds();
        // 선점된 좌석인지
        isReservedSeats(gameId, seatIds);
        //결제를 진행하는 유저가 해당 좌석을 선점한 유저인지
        isUserAuthorizedForPayment(gameId, seatIds, loginId);

        String orderId = generateOrderId();

        log.info("orderId =====> " + orderId);

        // 주문 생성
        orderService.createOrder(orderId, gameId, seatIds, loginId);

        // 결제 생성
        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(paymentRequestDto.getAmount())
                .status(PaymentStatus.REQUEST)
                .build();

        paymentRepository.save(payment);
    }

    // 결제 성공
    public  PaymentSuccessDto paymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = getPayment(orderId);
        Order order = orderService.getOrder(orderId);
        // 결제 요청된 금액과 실제 결제된 금액이 같은지 확인
        isVerifyPayment(payment, amount);

        payment.setPaymentKey(paymentKey);

        paymentRepository.save(payment);

        // ticketService.createTicket(payment.getGame(), payment.getUser(), payment.getSeatIds());

        PaymentSuccessDto paymentSuccessDto = requestPaymentAccept(paymentKey, orderId, amount);

        if (paymentSuccessDto != null) {

            ticketService.createTicket(order);
            // 주문 완
        }
        return paymentSuccessDto;
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


    private void isReservedSeats(Long gameId, Set<Long> seatIds) {
        for (Long id : seatIds) {
            String key = SEAT_LOCK + gameId + id;
            RLock lock = redissonClient.getLock(key);

            if (!lock.isLocked()) {
                throw new KboTicketException(ErrorCode.NOT_FOUND_RESERVATION);
            }
        }
    }

    private void isUserAuthorizedForPayment(Long gameId, Set<Long> seatIds, String loginId) {
        for (Long id : seatIds) {
            String key = SEAT_LOCK + gameId + id;
            RBucket<ReservedSeatInfo> bucket = redissonClient.getBucket(key);

            ReservedSeatInfo seatInfo = bucket.get();
            String reservedId = seatInfo.getEmail();
            if (!reservedId.equals(loginId)) {
                throw new KboTicketException(ErrorCode.USER_NOT_AUTHORIZED);
            }
        }
    }

    // 결제 요청된 금액과 실제 결제된 금액이 같은지 확인
    public void isVerifyPayment(Payment payment, Long amount) {
        if (!payment.getAmount().equals(amount)) {
            log.info("!payment.getAmount().equals(amount)" + !payment.getAmount().equals(amount));
            throw new KboTicketException(ErrorCode.PAYMENT_AMOUNT_EXP);
        }
    }

    // 최종 결제 승인 요청을 보내기 위해 필요한 정보를 담아 post로 보냄
    public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        PaymentRequestInput paymentRequestInput = PaymentRequestInput.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();

        PaymentClient paymentClient = new PaymentClient(paymentConfig);

        PaymentSuccessDto result = null;

        try {
            result = paymentClient.requestPayment(paymentRequestInput);
        } catch (Exception e) {
            e.printStackTrace();
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

    private String generateOrderId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9);
    }

    private Payment getPayment(String orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.PAYMENT_NOT_FOUND);
        });
    }


}
