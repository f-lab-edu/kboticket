package com.kboticket.controller.ticket.dto;

import com.kboticket.domain.Ticket;
import com.kboticket.dto.TicketDto;
import com.kboticket.enums.TicketStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class TicketResponse {

    private String ticketNumber;
    private String name;
    private int price;
    private LocalDateTime issuedAt;
    private LocalDateTime cancelAvailabledAt;
    private LocalDateTime canceledAt;
    private Boolean isCanceled;
    private String status;

    public static TicketResponse from (TicketDto ticketDto) {
        return TicketResponse.builder()
                .ticketNumber(ticketDto.getTicketNumber())
                .name(ticketDto.getName())
                .price(ticketDto.getPrice())
                .issuedAt(ticketDto.getIssuedAt())
                .cancelAvailabledAt(ticketDto.getCancelAvailableAt())
                .canceledAt(ticketDto.getCanceledAt())
                .isCanceled(ticketDto.getIsCanceled())
                .status(ticketDto.getStatus().name())
                .build();
    }
}
