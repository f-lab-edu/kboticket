package com.kboticket.dto.order;

import com.kboticket.domain.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    /* 주문번호 */
    private String orderId;
    /* 주문_명 */
    private String name;
    /* 주문자_이메일 */
    private String email;
    /* 경기_시작_시간 */
    private String startTime;
    /* 경기장 */
    private String stadiumName;
    /* 관람일시 */
    private String gameDate;
    /* 예매일시 */
    private LocalDateTime orderDate;
    /* 주문_상태 */
    private OrderStatus status;
    /* 주문_매수 */
    private Long cnt;


    /************************
     *  티켓 정보
     ************************/
    private String seatLevel;
    private String seatBlock;
    private String seatNumber;
    private Integer price;
    private String ticketNumber;
    private LocalDateTime cancelAvailableAt;
    private Boolean isCanceled;


    /************************
     *  결제 정보
     ************************/
    /* 주문_긍맥 */
    private Long amout;
    /* 주문_일시 */
    private LocalDateTime approvedAt;

}
