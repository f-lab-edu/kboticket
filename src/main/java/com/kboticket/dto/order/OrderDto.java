package com.kboticket.dto.order;

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
    /* 주문일자 */
    private LocalDateTime orderDate;
    /* 주문_상태 */
    private OrderStatus status;
    /* 매수 */
    private Long cnt;
}
