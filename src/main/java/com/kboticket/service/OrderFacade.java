package com.kboticket.service;

import com.kboticket.domain.*;
import com.kboticket.dto.ReservedSeatInfo;
import com.kboticket.dto.TicketDto;
import com.kboticket.dto.payment.*;
import com.kboticket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final GameService gameService;
    private final UserService userService;
    private final SeatService seatService;

    private final OrderService orderService;
    private final TicketService ticketService;
    private final PaymentService paymentService;

    private final RedissonClient redissonClient;

    // 주문 생성 및 결제 요청
    public void createOrderAndRequestPayment(PaymentRequestDto paymentRequestDto, String loginId) {
        Long gameId = paymentRequestDto.getGameId();

        // 내가 건 락이 없거나 실패한 경우 추가

        // 유저가 선택한 좌석 목
        Set<Long> seatIds = checkUserSelectedSeats(loginId, gameId);

        Game game = gameService.getGame(gameId);
        User user = userService.getUserByEmail(loginId);

        List<Seat> seats = seatIds.stream()
                .map(seatService::getSeat)
                .collect(Collectors.toList());

        String orderId = generateOrderId();

        orderService.createOrder(orderId, game, seats, user);
        paymentService.requestPayment(game, seats, user, paymentRequestDto.getAmount(), orderId);
    }

    private Set<Long> checkUserSelectedSeats(String loginId, Long gameId) {
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

    private String generateOrderId() {
        Random random = new Random();
        StringBuilder orderId = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10);
            orderId.append(digit);
        }
        return orderId.toString();
    }

    // 결제 성공
    @Transactional
    public PaymentSuccessResponse handlePaymentSuccess(String paymentKey, String orderId, Long amount) {
        // 한 번에  기록 하는게 좋다
        Order order = orderService.getOrder(orderId);
        PaymentSuccessResponse paymentSuccessResponse = paymentService.paymentSuccess(paymentKey, order, amount);

        if (paymentSuccessResponse != null) {
            orderService.completeOrder(order);
            ticketService.createTicket(order);
        }

        return paymentSuccessResponse;
    }

    // 결제 실패
    @Transactional
    public PaymentFailResponse handlePaymentFailure(String code, String orderId, String message) {
        return paymentService.paymentFail(code, orderId, message);
    }

    // 결제 취소
    // 가장 오류가 많이 나는 것을 먼저 처리하고
    public PaymentCancelResponse cancelTickets(PaymentCancelInput paymentCancelInput) {
        String orderId = paymentCancelInput.getOrderId();
        Long[] ticketIds = paymentCancelInput.getTicketId();
        int cancelAmount = paymentCancelInput.getCancelAmount();

        Order order = orderService.getOrder(orderId);

        Payment payment = paymentService.getPayment(orderId);
        boolean isAllTicketCancelled = areAllTicketIdsMatching(ticketIds, order);

        PaymentCancelResponse paymentCancelResponse = paymentService.cancel(order, payment, isAllTicketCancelled, cancelAmount);

        if (paymentCancelResponse != null) {
            // 민약 여기서 에러가 나면? 에러찍기
            ticketService.cancelTickets(order, ticketIds);
        }
        return paymentCancelResponse;
    }

    private boolean areAllTicketIdsMatching(Long[] ticketIds, Order order) {
        Set<Long> existingTicketIds = ticketService.getTickets(order).stream()
                .map(TicketDto::getId)
                .collect(Collectors.toSet());

        return Arrays.stream(ticketIds)
                .allMatch(existingTicketIds::contains);
    }

    // 주문 상세

    // 주문 > 티켓 내역
    public List<TicketDto> getTickets(String orderId) {
        Order order = orderService.getOrder(orderId);

        return ticketService.getTickets(order);
    }
}
