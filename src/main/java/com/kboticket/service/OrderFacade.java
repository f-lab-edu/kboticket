package com.kboticket.service;

import com.kboticket.domain.*;
import com.kboticket.dto.OrderResponse;
import com.kboticket.dto.TicketDto;
import com.kboticket.dto.payment.*;
import com.kboticket.enums.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
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

    // 주문 생성 및 결제 요청
    public void createOrderAndRequestPayment(PaymentRequestDto paymentRequestDto, String loginId) {
        String orderId = paymentRequestDto.getOrderId();
        Long gameId = paymentRequestDto.getGameId();
        Set<Long> seatIds = paymentRequestDto.getSeatIds();

        Game game = gameService.getGame(gameId);
        User user = userService.getUser(loginId);

        List<Seat> seats = seatIds.stream()
                .map(id -> seatService.getSeat(id))
                .collect(Collectors.toList());

        orderService.createOrder(orderId, game, seats, user);
        paymentService.requestPayment(game, seats, user, paymentRequestDto.getAmount());
    }

    // 결제 성공
    @Transactional
    public PaymentSuccessResponse handlePaymentSuccess(String paymentKey, String orderId, Long amount) {
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
    public PaymentCancelResponse cancelTickets(String orderId, Long[] ticketIds) {
        Order order = orderService.getOrder(orderId);

        ticketService.cancelTickets(order, ticketIds);

        boolean isAllTicketCancelled = ticketService.getTickets(order).stream()
                .allMatch(ticket -> ticket.getStatus() == TicketStatus.CANCELLED);

        Payment payment = paymentService.getPayment(orderId);

        return paymentService.cancel(order, payment, isAllTicketCancelled);

    }

    // 주문 내역
    public OrderResponse getOrderList(String email) {
        User user = userService.getUser(email);
        return orderService.getOrderList(user);
    }

    public List<TicketDto> getTickets(String orderId) {
        Order order = orderService.getOrder(orderId);

        return ticketService.getTickets(order);
    }
}
