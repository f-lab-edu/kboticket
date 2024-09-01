package com.kboticket.controller.ticket;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.TicketDto;
import com.kboticket.dto.payment.PaymentCancelInput;
import com.kboticket.dto.payment.PaymentCancelResponse;
import com.kboticket.service.OrderFacade;
import lombok.RequiredArgsConstructor;
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
    @PostMapping("/cancel")
    public CommonResponse<PaymentCancelResponse> cancelTicket(@RequestBody PaymentCancelInput paymentCancelInput) {
        PaymentCancelResponse response = orderFacade.cancelTickets(paymentCancelInput);

        return new CommonResponse<>(response);
    }
}
