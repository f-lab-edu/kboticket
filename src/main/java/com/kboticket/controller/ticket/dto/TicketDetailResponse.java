package com.kboticket.controller.ticket.dto;

import com.kboticket.domain.OrderSeat;
import com.kboticket.enums.TicketStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class TicketDetailResponse {
    private Long id;
    private String ticketNumber;
    private OrderSeat orderSeat;
    private String title;
    private int price;

    private LocalDateTime issuedAt;
    private LocalDateTime cancelAvailableAt;
    private LocalDateTime canceledAt;

    private Boolean isCanceled;
    private TicketStatus status;
}
