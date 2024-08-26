package com.kboticket.dto.order;

import com.kboticket.domain.Order;
import com.kboticket.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    /* 주문번호 */
    private String orderId;
    /* 주문_명 */
    private String name;
    /* 관람일시 */
    private String gameDate;
    /* 예매일시 */
    private LocalDateTime orderDate;
    /* 결제 수단 */
    private String method;
    /* 주문_상태 */
    private OrderStatus status;
    /* 매수 */
    private Long cnt;

    public static OrderDto from(Order order) {
        return OrderDto.builder()
                .orderId(order.getId())
                .name(order.getName())
                .gameDate(order.getGame().getGameDate())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .cnt(order.getCnt())
                .build();
    }
}
