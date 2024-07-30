package com.kboticket.service;

import com.kboticket.domain.*;
import com.kboticket.dto.TicketDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TicketStatus;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.GameRepository;
import com.kboticket.repository.SeatRepository;
import com.kboticket.repository.TicketRepository;
import com.kboticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;

    public Ticket findOne(Long itemId, User user) {
        return ticketRepository.findByIdAndUser(itemId, user)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_TICKET));
    }

    public List<TicketDto> findAll() {
        List<Ticket> ticketList = ticketRepository.findAll();

        List<TicketDto> ticketDtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            TicketDto ticketDto = TicketDto.builder()
                    .id(ticket.getId())
                    .game(ticket.getGame())
                    .seat(ticket.getSeat())
                    .order(ticket.getOrder())
                    .user(ticket.getUser())
                    .reserved(TicketStatus.RESERVED)
                    .build();

            ticketDtoList.add(ticketDto);
        }
        return ticketDtoList;
    }

    // 좌석 지정 -> 티켓 생성
    public void createTicket(Order order) {
//        String orderId = order.getId();
//
//        // seatlist 가져오기
//
//       // String[] seatIdsArr = seatIds.split(",");
//
//        List<Ticket> tickets = Arrays.stream(seatIdsArr)
//                .map(id -> {
//                    Long seatId = Long.valueOf(id);
//                    Seat seat = seatRepository.getReferenceById(seatId);
//                    return Ticket.builder()
//                            .game(order.getGame())
//                            .seat(seat)
//                            .user(order.getUser())
//                            .reserved(TicketStatus.RESERVED)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        ticketRepository.saveAll(tickets);

    }

    public void reserveTicket(Long[] seatIds) {
        List<Ticket> tickets = Arrays.stream(seatIds)
                .map(seatId -> {
                    Ticket ticket = ticketRepository.getReferenceById(seatId);
                    ticket.setReserved(TicketStatus.RESERVED);

                    return ticket;
                })
                .collect(Collectors.toList());

        ticketRepository.saveAll(tickets);

    }

    public void cancelTicket(Long ticketId, Long userId) {
    }

}
