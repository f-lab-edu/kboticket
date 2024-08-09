package com.kboticket.dto;

import lombok.*;

import java.util.List;


@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private List<OrdersDto> orders;
}
