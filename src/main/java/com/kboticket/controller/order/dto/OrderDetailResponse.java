package com.kboticket.controller.order.dto;

import com.kboticket.domain.OrderSeat;
import com.kboticket.domain.OrderStatus;
import com.kboticket.dto.order.OrderDetailDto;
import com.kboticket.dto.order.OrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {

    // 주문 정보
    private String name;
    private String gameDate;
    private String stadium;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private String email;
    private String startTime;

    // 티켓 정보
    private String seatLevel;
    private String seatBlock;
    private String seatNumber;
    private int price;
    private String ticketNumber;
    private LocalDateTime cancelAvailableAt;
    private Boolean isCanceled;

    // 결제 정보
    private Long amount;
    private LocalDateTime approvedAt;

    private OrderSeat orderSeat;

    public static OrderDetailResponse from(OrderDto dto) {
        return builder()
                .name(dto.getName())
                .gameDate(dto.getGameDate())
                .stadium(dto.getStadiumName())
                .orderDate(dto.getOrderDate())
                .status(dto.getStatus())
                .email(dto.getEmail())
                .startTime(dto.getStartTime())
                .build();
    }

    public static OrderDetailResponse from(OrderDetailDto dto) {
        return builder()
            .seatLevel(dto.getSeatLevel())
            .seatBlock(dto.getSeatBlock())
            .seatNumber(dto.getSeatNumber())
            .price(dto.getPrice())
            .ticketNumber(dto.getTicketNumber())
            .cancelAvailableAt(dto.getCancelAvailableAt())
            .isCanceled(dto.getIsCanceled())
            .price(dto.getPrice())
            .amount(dto.getAmount())
            .approvedAt(dto.getApprovedAt())
            .build();
    }
}
