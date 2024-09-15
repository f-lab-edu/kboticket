package com.kboticket.controller.ticket;

import com.kboticket.common.CommonResponse;
import com.kboticket.controller.ticket.dto.TicketResponse;
import com.kboticket.dto.TicketDto;
import com.kboticket.dto.payment.PaymentCancelRequest;
import com.kboticket.dto.payment.PaymentCancelResponse;
import com.kboticket.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * 티켓 상세
     */
    @GetMapping("/{ticketId}")
    public CommonResponse<TicketResponse> view(@PathVariable Long tickketId) {
        TicketDto ticketDto = ticketService.getTicket(tickketId);

        TicketResponse response = TicketResponse.from(ticketDto);

        return new CommonResponse<>(response);
    }

    /**
     * 티켓 취소
     */
    @PostMapping("/cancel")
    public CommonResponse<PaymentCancelResponse> cancelTicket(@RequestBody PaymentCancelRequest request) {
        PaymentCancelResponse response = ticketService.cancel(request);

        return new CommonResponse<>(response);
    }
}
