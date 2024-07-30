package com.kboticket.service;

import com.kboticket.domain.*;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.ReservationStatus;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final GameService gameService;
    private final UserService userService;
    private final SeatService seatService;

    private final OrderRepository orderRepository;

    // 주문 생성
    public void createOrder(String orderId, Long gameId, Set<Long> seatIds, String loginId) {
        Game game = gameService.getGame(gameId);
        User user = userService.getUser(loginId);

        List<Seat> seats = seatIds.stream()
                .map(id -> {
                    return seatService.getSeat(id);
                })
                .collect(Collectors.toList());

        Order order = Order.builder()
                .id(orderId)
                .game(game)
                .user(user)
                .status(OrderStatus.ORDER)
                .build();

        List<OrderSeat> orderSeats = new ArrayList<>();
        for (Seat seat : seats) {
            OrderSeat orderSeat = OrderSeat.createOrderSeat(seat, order);
            orderSeats.add(orderSeat);
        }

        order.setOrderSeats(orderSeats);

        orderRepository.save(order);
    }

    // 주문 취소
    public void cancelOrder(Long orderId) {


    }

    // 주문 상태 update
    public void updateOrderStatus(ReservationStatus status) {

    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_ORDER);
        });
    }
}


