package com.kboticket.dto.payment;

import lombok.*;

import java.util.Set;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private String payType;
    private Long amount;
    private String orderId;
    private Long gameId;
    private Set<Long> seatIds;

}
