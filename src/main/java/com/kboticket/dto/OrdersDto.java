package com.kboticket.dto;

import com.kboticket.domain.Game;
import com.kboticket.domain.OrderSeat;
import com.kboticket.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDto {
    /* 주문번호 */
    private String orderId;
    /* 주문_명 */
    private String title;
    /* 매수 */
    private String cnt;
    /* 좌석 */
    private List<OrderSeat> orderSeats;
    /* 주문_상태 */
    private OrderStatus status;
    /* 주문일자 */
    private LocalDateTime orderDate;
    /* 취소_가능일 */
    private String cacelAvailDate;
    /* 관람일시 */
    private String gameDate;

    private Game game;
}
