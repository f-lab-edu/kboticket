package com.kboticket.controller.ticket;

import com.kboticket.common.CommonResponse;
import com.kboticket.controller.ticket.dto.TicketResponse;
import com.kboticket.dto.TicketDto;
import com.kboticket.dto.payment.PaymentCancelInput;
import com.kboticket.dto.payment.PaymentCancelResponse;
import com.kboticket.service.OrderFacade;
import com.kboticket.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final OrderFacade orderFacade;
    private final TicketService ticketService;

    /**
     * 티켓 상세
     */
    @GetMapping("/{ticketId}")
    public CommonResponse<TicketResponse> list(@PathVariable Long tickketId) {
        TicketDto ticketDto = ticketService.getTicket(tickketId);

        TicketResponse response = TicketResponse.from(ticketDto);

        return new CommonResponse<>(response);
    }

    /**
     * 티켓 취소
     */
    @PostMapping("/cancel")
    public CommonResponse<PaymentCancelResponse> cancelTicket(@RequestBody PaymentCancelInput paymentCancelInput) {
        PaymentCancelResponse response = orderFacade.cancelTickets(paymentCancelInput);

        return new CommonResponse<>(response);
    }
}
