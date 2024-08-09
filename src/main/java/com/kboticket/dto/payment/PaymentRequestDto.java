package com.kboticket.dto.payment;

import lombok.*;

import java.util.Set;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {

    private String orderId;
    private Long gameId;
    private Set<Long> seatIds;
    private Long amount;

}
