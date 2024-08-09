package com.kboticket.dto;

import com.kboticket.domain.*;
import com.kboticket.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketDto {

    private Long id;
    private OrderSeat orderSeat;
    private String ticketNumber;
    private String title;
    private LocalDateTime issuedAt;
    private TicketStatus status;

}
