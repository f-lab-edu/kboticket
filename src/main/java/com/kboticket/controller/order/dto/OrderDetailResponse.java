package com.kboticket.controller.order.dto;

import com.kboticket.domain.OrderSeat;
import com.kboticket.domain.OrderStatus;
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
    private int amout;
    private LocalDateTime approvedAt;

    private OrderSeat orderSeat;

    public static OrderDetailResponse from(OrderDto orderDto) {
        return builder()
                .name(orderDto.getName())
                .gameDate(orderDto.getGameDate())
                .stadium(orderDto.getStadiumName())
                .orderDate(orderDto.getOrderDate())
                .status(orderDto.getStatus())
                .email(orderDto.getEmail())
                .startTime(orderDto.getStartTime())
                .seatLevel(orderDto.getSeatLevel())
                .seatBlock(orderDto.getSeatBlock())
                .seatNumber(orderDto.getSeatNumber())
                .price(orderDto.getPrice())
                .ticketNumber(orderDto.getTicketNumber())
                .cancelAvailableAt(orderDto.getCancelAvailableAt())
                .isCanceled(orderDto.getIsCanceled())
                .build();
    }
}
