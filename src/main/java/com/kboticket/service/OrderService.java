package com.kboticket.service;

import com.kboticket.domain.*;
import com.kboticket.repository.GameRepository;
import com.kboticket.repository.OrderRepository;
import com.kboticket.repository.SeatRepository;
import com.kboticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final SeatRepository seatRepository;

    /**
     * 티켓 예매
     */
    @Transactional
    public Long order(Long userId, Long gameId, Long[] seatIds) {
        // 엔티티 조회
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();
        Game game = gameRepository.findOne(gameId);

        Ticket ticket = new Ticket();
        List<Ticket> ticketList = new ArrayList<>();
        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findOne(seatId);
            // 티켓 생성
            ticket = ticket.createTicket(game, seat);
            ticketList.add(ticket);
        }
        // 얘매 생성
        Order order = Order.createOrder(user, ticketList);
        // 예매 저장
        orderRepository.save(order);

        return order.getId();

    }

    /**
     * 예매 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        List<Ticket> tickets = order.getTickets();
        for (Ticket ticket : tickets) {
            // ticket -> cancel
            ticket.cancel();
        }
        // 주문 취소
        order.cancel();
    }
}
