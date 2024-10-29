package com.kboticket.dto.order;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {

    /************************
     *  티켓 정보
     ************************/
    private String seatLevel;
    private String seatBlock;
    private String seatNumber;
    private int price;
    private String ticketNumber;
    private LocalDateTime cancelAvailableAt;
    private Boolean isCanceled;


    /************************
     *  결제 정보
     ************************/
    /* 주문_긍맥 */
    private Long amount;
    /* 주문_일시 */
    private LocalDateTime approvedAt;

}
