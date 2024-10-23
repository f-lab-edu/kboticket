package com.kboticket.service.payment;

import com.kboticket.common.constants.KboConstant;
import com.kboticket.config.PaymentConfig;
import com.kboticket.config.payment.PaymentClient;
import com.kboticket.domain.*;
import com.kboticket.dto.ReservedSeatInfo;
import com.kboticket.dto.payment.*;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.PaymentStatus;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.PaymentRepository;
import com.kboticket.repository.SeatRepository;
import com.kboticket.repository.UserRepository;
import com.kboticket.repository.game.GameRepository;
import com.kboticket.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    /**
     *  결제 요청
     */
    public void requestPayment(Game game, Set<Long> seatIds, User user, Long amount, String orderId) {
        Long gameId = game.getId();

        // 선점된 좌석인지
        isReservedSeats(gameId, seatIds);
        //결제를 진행하는 유저가 해당 좌석을 선점한 유저인지
        isUserAuthorizedForPayment(gameId, seatIds, user.getEmail());

        String orderNm = "";

        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_ORDER);
        });

        // 결제 생성
        Payment payment = Payment.builder()
                .order(order)
                .orderNm(orderNm)
                .amount(amount)
                .status(PaymentStatus.REQUEST)
                .build();

        paymentRepository.save(payment);
    }

    /**
     *  결제 성공
     */
    public PaymentSuccessResponse paymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = getPayment(orderId);
        // 결제 요청된 금액과 실제 결제된 금액이 같은지 확인
        isVerifyPayment(payment, amount);

        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_ORDER);
        });

        PaymentSuccessResponse response = requestPaymentAccept(paymentKey, orderId, amount);

        if (response == null) {
            throw new KboTicketException(ErrorCode.PAYMENT_FAILURE);
        }

        // order 상태 변경
        String title = order.getGame().getHomeTeam().getName() + " VS " +  order.getGame().getAwayTeam().getName();
        order.setStatus(OrderStatus.COMPLETE);
        order.setOrderDate(LocalDateTime.now());
        order.setName(title);

        payment.setPaymentKey(paymentKey);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setOrder(order);

        paymentRepository.save(payment);

        return response;
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
        PaymentCancelRequest input = PaymentCancelRequest.builder()
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
        PaymentCancelRequest input = PaymentCancelRequest.builder()
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

    // 최종 결제 승인 요청을 보내기 위해 필요한 정보를 담아 post로 보냄s
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

    public void createOrderAndRequestPayment(String loginId, Long gameId, Long amount) {
        Set<Long> seatIds = checkUserSelectedSeats(loginId, gameId);

        User user = userRepository.findByEmail(loginId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_USER);
        });
        Game game = gameRepository.findById(gameId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_GAME);
        });

        String orderId = generateOrderId();

        Order order = Order.createOrder(orderId, game, user);
        List<OrderSeat> orderSeats = seatIds.stream()
                .map(seatId -> {
                    Seat seat = seatRepository.findById(seatId).orElseThrow(() -> {
                        throw new KboTicketException(ErrorCode.NOT_FOUND_ORDER);
                    });

                    OrderSeat orderSeat = OrderSeat.createOrderSeat(seat, order);
                    return orderSeat;
                })
                .collect(Collectors.toList());

        order.setOrderSeats(orderSeats);

        orderRepository.save(order);

        requestPayment(game, seatIds, user, amount, orderId);
    }

    private String generateOrderId() {
        Random random = new Random();
        StringBuilder orderId = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10);
            orderId.append(digit);
        }
        return orderId.toString();
    }

    public Set<Long> checkUserSelectedSeats(String loginId, Long gameId) {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> keysIterable = keys.getKeysByPattern("seatLock:" + gameId + "*");
        Set<Long> seatIds = new HashSet<>();
        for (String key : keysIterable) {
            RBucket<ReservedSeatInfo> seatBucket = redissonClient.getBucket(key);
            ReservedSeatInfo data = seatBucket.get();
            if (data != null && data.getEmail().equals(loginId)) {
                seatIds.add(data.getSeatId());
            }
        }
        return seatIds;
    }
}
