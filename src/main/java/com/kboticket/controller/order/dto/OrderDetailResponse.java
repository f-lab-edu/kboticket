package com.kboticket.controller.order.dto;

import com.kboticket.dto.TicketDto;
import com.kboticket.dto.payment.PaymentDto;
import com.kboticket.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class OrderDetailResponse {

    private Long orderId;
    private LocalDateTime orderDate;
    private String status;

    private UserDto user;
    private List<TicketDto> tickets;
    private PaymentDto paymentDetails;

}
