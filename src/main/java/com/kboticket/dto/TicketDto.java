package com.kboticket.dto;

import com.kboticket.domain.*;
import com.kboticket.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDto {

    private Long id;
    private Game game;
    private Order order;
    private Seat seat;
    private User user;
    private TicketStatus reserved;

}
