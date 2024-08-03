package com.kboticket.dto;

import com.kboticket.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private List<Order> orders;

}
