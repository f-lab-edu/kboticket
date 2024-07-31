package com.kboticket.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class OrdersDto {
    /* 주문번호 */
    private String orderId;
    /* 주문_명 */
    private String title;
    /* 매수 */
    private String cnt;
    /* 주문_상태 */
    private String status;
    /* 주문일자 */
    private LocalDateTime orderDate;
    /* 취소_가능일 */
    private String cacelAvailDate;
    /* 관람일시 */
    private String gameDate;
}
