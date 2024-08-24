package com.kboticket.service;

import com.kboticket.common.constants.KboConstant;
import com.kboticket.config.PaymentConfig;
import com.kboticket.config.payment.PaymentClient;
import com.kboticket.domain.*;
import com.kboticket.dto.payment.PaymentRequestInput;
import com.kboticket.dto.ReservedSeatInfo;
import com.kboticket.dto.payment.*;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.PaymentStatus;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.order.OrderRepository;
import com.kboticket.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final RedissonClient redissonClient;
    private final PaymentConfig paymentConfig;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    /**
     *  결제 요청
     */
    public void requestPayment(Game game, List<Seat> seats, User user, Long amount, String orderId) {
        Long gameId = game.getId();
        Set<Long> seatIds = seats.stream()
                .map(seat -> seat.getId())
                .collect(Collectors.toSet());

        // 선점된 좌석인지
        isReservedSeats(gameId, seatIds);
        //결제를 진행하는 유저가 해당 좌석을 선점한 유저인지
        isUserAuthorizedForPayment(gameId, seatIds, user.getEmail());

        String orderNm = "";

        // 결제 생성
        Payment payment = Payment.builder()
                .orderId(orderId)
                .orderNm(orderNm)
                .amount(amount)
                .status(PaymentStatus.REQUEST)
                .build();

        paymentRepository.save(payment);
    }

    /**
     *  결제 성공
     */
    public PaymentSuccessResponse paymentSuccess(String paymentKey, Order order, Long amount) {
        Payment payment = getPayment(order.getId());
        // 결제 요청된 금액과 실제 결제된 금액이 같은지 확인
        isVerifyPayment(payment, amount);

        payment.setPaymentKey(paymentKey);
        payment.setStatus(PaymentStatus.COMPLETED);

        paymentRepository.save(payment);

        return requestPaymentAccept(paymentKey, order.getId(), amount);
    }

    /**
     * 결제 실패
     */
    public PaymentFailResponse paymentFail(String code, String orderId, String message) {
        PaymentFailResponse response = PaymentFailResponse.builder()
                .code(code)
                .orderId(orderId)
                .message(message)
                .build();

        Payment payment = getPayment(orderId);
        payment.setFailReason(message);

        paymentRepository.save(payment);

        return response;
    }

    /**
     * 결제 취소 - 부분
     */
    public PaymentCancelResponse paymentCancelPart(Payment payment, String cancelReason, int cancelAmount) {
        String paymentKey = payment.getPaymentKey();
        PaymentCancelInput input = PaymentCancelInput.builder()
                .paymentKey(paymentKey)
                .cancelReason(cancelReason)
                .cancelAmount(cancelAmount)
                .build();

        PaymentClient paymentClient = new PaymentClient(paymentConfig);

        PaymentCancelResponse result = paymentClient.cancelPayment(input);
        if (result == null) {
            throw new KboTicketException(ErrorCode.PAYMENT_CANCEL_EXCEPTION);
        }

        payment.setStatus(PaymentStatus.CANCELLED_PART);
        payment.setCancelReason(cancelReason);

        paymentRepository.save(payment);

        return result;
    }

    /**
     * 결제 취소 - 전체
     */
    public PaymentCancelResponse paymentCancelAll(Payment payment, String cancelReason) {
        String paymentKey = payment.getPaymentKey();
        PaymentCancelInput input = PaymentCancelInput.builder()
                .paymentKey(paymentKey)
                .cancelReason(cancelReason)
                .build();

        PaymentClient paymentClient = new PaymentClient(paymentConfig);

        PaymentCancelResponse result = paymentClient.cancelPayment(input);
        if (result == null) {
            throw new KboTicketException(ErrorCode.PAYMENT_CANCEL_EXCEPTION);
        }

        payment.setStatus(PaymentStatus.CANCELLED_ALL);
        payment.setCancelReason(cancelReason);

        paymentRepository.save(payment);

        return result;
    }

    private void isReservedSeats(Long gameId, Set<Long> seatIds) {
        for (Long id : seatIds) {
            String key = KboConstant.SEAT_LOCK + gameId + id;
            RLock lock = redissonClient.getLock(key);
            if (!lock.isLocked()) {
                throw new KboTicketException(ErrorCode.NOT_FOUND_RESERVATION);
            }
        }
    }

    private void isUserAuthorizedForPayment(Long gameId, Set<Long> seatIds, String loginId) {
        for (Long id : seatIds) {
            String key = KboConstant.SEAT_LOCK + gameId + id;
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
    public PaymentSuccessResponse requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        PaymentRequestInput paymentRequestInput = PaymentRequestInput.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();

        PaymentClient paymentClient = new PaymentClient(paymentConfig);

        PaymentSuccessResponse result = null;
        try {
            result = paymentClient.requestPayment(paymentRequestInput);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    public Payment getPayment(String orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.PAYMENT_NOT_FOUND);
        });
    }

    public PaymentCancelResponse cancel(Order order, Payment payment, boolean isAllTicketCancelled, int cancelPrice) {
        String cancelReason = "USER_REQUEST";

        PaymentCancelResponse response = null;
        if (isAllTicketCancelled) {
            order.setStatus(OrderStatus.CANCELLED_ALL);     // 전체 취소인 경우
            response = paymentCancelAll(payment, cancelReason);

        } else {

            order.setStatus(OrderStatus.CANCELLED_PART);    // 부분 취소인 경우
            response = paymentCancelPart(payment, cancelReason, cancelPrice);
        }

        orderRepository.save(order);

        return response;
    }
}
