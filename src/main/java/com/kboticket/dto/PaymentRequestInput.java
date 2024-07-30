package com.kboticket.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestInput {

    private String orderId;
    private String paymentKey;
    private Long amount;

}
