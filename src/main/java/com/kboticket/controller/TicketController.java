package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.common.constants.ResponseCode;
import com.kboticket.domain.Ticket;
import com.kboticket.domain.User;
import com.kboticket.dto.TicketDto;
import com.kboticket.service.SeatService;
import com.kboticket.service.TicketService;
import com.kboticket.service.UserService;
import io.netty.handler.ssl.OpenSslSessionTicketKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private TicketService ticketService;
    private SeatService seatService;
    private UserService userService;

    /**
     *  티켓 목록
     */
    @GetMapping("/list")
    public CommonResponse<List<TicketDto>> getTickets() {

        List<TicketDto> ticketDto = ticketService.findAll();

        return new CommonResponse<>(ticketDto);
    }

    /**
     * 티켓 정보 조회
     */
    @GetMapping("/{ticketId}")
    public CommonResponse<Ticket> getTicketInfo(@RequestParam Long tickketId,
                                                @RequestParam Long userId) {
        User user = userService.findById(userId);
        Ticket ticket = ticketService.findOne(tickketId, user);

        return new CommonResponse<>(ticket);
    }

    /**
     * 티켓 예약
     */
   @PostMapping("/reserve")
    public void reserveTicket(@RequestParam Long[] seatIds) {

        ticketService.reserveTicket(seatIds);
    }

    /**
     * 티켓 예약 취소
     */
    @PostMapping("/cancel")
    public void reserveTicket(@RequestParam Long tickettId,
                              @RequestParam Long userId) {
        ticketService.cancelTicket(tickettId, userId);
    }
}
