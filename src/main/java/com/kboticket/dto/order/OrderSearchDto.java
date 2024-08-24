package com.kboticket.dto.order;

import com.kboticket.controller.order.dto.OrderListRequest;
import com.kboticket.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSearchDto {

    private String email;
    private String periodType;  // type : 15/ 1/ 2/ 3
    private String dataType;    // type : orderDate/ gameDate
    private Integer year;
    private Integer month;
    private OrderStatus orderStatus;

    public static OrderSearchDto of(String email, OrderListRequest orderListRequest) {
        return OrderSearchDto
                .builder()
                .email(email)
                .periodType(orderListRequest.getPeriodType())
                .dataType(orderListRequest.getDataType())
                .year(orderListRequest.getYear())
                .month(orderListRequest.getMonth())
                .orderStatus(orderListRequest.getOrderStatus())
                .build();
    }
}
