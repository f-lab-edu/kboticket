package com.kboticket.controller.order.dto;

import com.kboticket.dto.TicketDto;
import com.kboticket.dto.order.OrderDto;
import com.kboticket.dto.payment.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class OrderDetailResponse {

    private OrderDto order;
    private List<TicketDto> tickets;
    private PaymentDto paymentDetails;

    public static OrderDetailResponse of(OrderDto order,
                                         PaymentDto payment,
                                         List<TicketDto> tickets) {
        return builder()
                .order(order)
                .tickets(tickets)
                .paymentDetails(payment)
                .build();
    }

}
