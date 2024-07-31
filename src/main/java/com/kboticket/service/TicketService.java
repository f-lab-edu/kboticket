package com.kboticket.service;

import com.kboticket.domain.Order;
import com.kboticket.domain.OrderSeat;
import com.kboticket.domain.Ticket;
import com.kboticket.domain.User;
import com.kboticket.dto.TicketDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TicketStatus;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final OrderService orderService;

    public Ticket findOne(Long itemId, User user) {
        return ticketRepository.findByIdAndUser(itemId, user)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_TICKET));
    }

    // 티켓 생성
    public void createTicket(Order order) {
        String orderId = order.getId();

        List<OrderSeat> orderSeats = orderService.getOrderSeats(orderId);

        List<Ticket> tickets = orderSeats.stream()
                .map(orderSeat -> {
                    return Ticket.builder()
                            .orderSeat(orderSeat)
                            .status(TicketStatus.ISSUED)
                            .issuedAt(LocalDateTime.now())
                            .build();
                })
                .collect(Collectors.toList());

        ticketRepository.saveAll(tickets);
    }

    public void cancelTicket(Long ticketId, Long userId) {

    }

    // 티켓 록록
    public List<TicketDto> getTickets(String orderId) {
        List<OrderSeat> orderSeats = orderService.getOrderSeats(orderId);

        List<TicketDto> tickets = orderSeats.stream()
                .map(orderSeat -> {
                    Ticket ticket = ticketRepository.findByOrderSeat(orderSeat);

                    return TicketDto.builder()
                            .orderSeat(ticket.getOrderSeat())
                            .ticketNumber(ticket.getTicketNumber())
                            .title(ticket.getTitle())
                            .status(ticket.getStatus())
                            .issuedAt(ticket.getIssuedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return tickets;
    }
}
