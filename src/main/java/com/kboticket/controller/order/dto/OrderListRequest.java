package com.kboticket.controller.order.dto;

import com.kboticket.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderListRequest {
    private OrderStatus orderStatus;
    private String periodType;
    private String dataType;
    private Integer year;
    private Integer month;
}
