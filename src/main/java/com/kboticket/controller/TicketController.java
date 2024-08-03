package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.TicketDto;
import com.kboticket.service.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final OrderFacade orderFacade;

    /**
     * 티켓 목록
     */
    @GetMapping("/{orderId}")
    public CommonResponse<List<TicketDto>> list(@PathVariable String orderId) {
        List<TicketDto> tickets = orderFacade.getTickets(orderId);

        return new CommonResponse<>(tickets);
    }

    /**
     * 티켓 취소
     */
    @PostMapping("/{ticketId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void cancelTicket(@RequestParam String orderId,
                             @RequestParam Long[] ticketId) {
        orderFacade.cancelTickets(orderId, ticketId);
    }
}
