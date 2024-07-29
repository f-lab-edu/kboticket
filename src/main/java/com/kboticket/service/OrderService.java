package com.kboticket.service;

import com.kboticket.domain.Order;
import com.kboticket.domain.Reservation;
import com.kboticket.domain.Ticket;
import com.kboticket.repository.OrderRepository;
import com.kboticket.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ReservationRepository reserveRepository;

    public Long order(Long userId, String reservationId) {

        List<Reservation> reservations = reserveRepository.findById(reservationId);

        List<Ticket> ticketList = new ArrayList<>();
        for (Reservation reservation : reservations) {
            // 티켓 생성
            Ticket ticket = Ticket.builder()
                    .seat(reservation.getSeat())
                    .game(reservation.getGame())
                    .user(reservation.getUser())
                    .build();
            ticketList.add(ticket);
        }

        Order order = Order.createOrder(ticketList.get(0).getUser(), ticketList);

        // 예매 저장
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 예매 취소
     */
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order = optionalOrder.get();

        List<Ticket> tickets = order.getTickets();
        for (Ticket ticket : tickets) {
            // ticket -> cancel
            //ticket.cancel();
        }

        // 주문 취소

    }
}
