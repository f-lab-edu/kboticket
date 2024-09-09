package com.kboticket.dto;

import com.kboticket.domain.*;
import com.kboticket.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TicketDto {

    private Long id;
    /* 티켓 번호 */
    private String ticketNumber;
    /* 좌석 정보 */
    private OrderSeat orderSeat;

    private String name;
    /* 티켓 가격 */
    private int price;
    /* 발급 일시 */
    private LocalDateTime issuedAt;
    /* 취소 가능일 */
    private LocalDateTime cancelAvailableAt;
    /* 취소일시 */
    private LocalDateTime canceledAt;
    /* 취소 여부 */
    private Boolean isCanceled;
    /* 티켓 상태 */
    private TicketStatus status;

    public static TicketDto from(Ticket ticket) {
        return TicketDto.builder()
                .ticketNumber(ticket.getTicketNumber())
                .name(ticket.getName())
                .price(ticket.getPrice())
                .issuedAt(ticket.getIssuedAt())
                .canceledAt(ticket.getCanceledAt())
                .cancelAvailableAt(ticket.getCancelAvailableAt())
                .isCanceled(ticket.getIsCanceled())
                .status(ticket.getStatus())
                .build();

    }

}
