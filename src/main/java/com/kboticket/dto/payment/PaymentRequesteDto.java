package com.kboticket.dto.payment;

import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequesteDto {

    private String orderId;
    // private String paymentKey;
    private Long gameId;
    private String seatIds;
    private Long amount;

}
