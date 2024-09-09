package com.kboticket.service.ticket;

import com.kboticket.domain.Order;
import com.kboticket.domain.OrderSeat;
import com.kboticket.domain.Ticket;
import com.kboticket.dto.TicketDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TicketStatus;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public void createTicket(Order order) {
        List<OrderSeat> orderSeats = order.getOrderSeats();
        List<Ticket> tickets = orderSeats.stream()
                .map(orderSeat -> Ticket.builder()
                            .orderSeat(orderSeat)
                            .status(TicketStatus.ISSUED)
                            .name(orderSeat.getSeat().getLevel() + " " +  orderSeat.getSeat().getBlock() + " " +  orderSeat.getSeat().getNumber())
                            .issuedAt(LocalDateTime.now())
                            .price(orderSeat.getSeat().getPrice())
                            .build())
                .collect(Collectors.toList());

        ticketRepository.saveAll(tickets);
    }

    public void cancelTickets(Order order, Long[] ticketIds) {
        // 티켓 상태 변경 (ISSUED -> CANCELLED)
        List<Ticket> ticketsToCancel = Arrays.stream(ticketIds)
                .map(id ->  {
                    return ticketRepository.findById(id).orElseThrow(() -> {
                        throw new KboTicketException(ErrorCode.NOT_FOUND_TICKET);
                    });
                }).collect(Collectors.toList());

        for (Ticket ticket : ticketsToCancel) {
            if (ticket.getStatus() == TicketStatus.CANCELLED) {
                throw new KboTicketException(ErrorCode.TICKET_ALREADY_CANCELLED);
            }
            ticket.setStatus(TicketStatus.CANCELLED);
        }

        ticketRepository.saveAll(ticketsToCancel);
    }

    // 티켓 록록
    public List<TicketDto> getTickets(Order order) {
        List<OrderSeat> orderSeats = order.getOrderSeats();

        List<TicketDto> tickets = orderSeats.stream()
                .map(orderSeat -> {
                    Ticket ticket = ticketRepository.findByOrderSeat(orderSeat);

                    return TicketDto.builder()
                            .orderSeat(ticket.getOrderSeat())
                            .ticketNumber(ticket.getTicketNumber())
                            .name(ticket.getName())
                            .status(ticket.getStatus())
                            .issuedAt(ticket.getIssuedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return tickets;
    }

    // 티켓 록록
    public TicketDto getTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_TICKET);
        });

        return TicketDto.from(ticket);
    }
}
