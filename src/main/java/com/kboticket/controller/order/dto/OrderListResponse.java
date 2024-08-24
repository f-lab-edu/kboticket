package com.kboticket.controller.order.dto;

import com.kboticket.dto.order.OrderDto;
import lombok.*;

import java.util.List;


@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {
    private List<OrderDto> orders;
}
