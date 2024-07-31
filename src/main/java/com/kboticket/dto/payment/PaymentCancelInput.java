package com.kboticket.dto.payment;

import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor
public class PaymentCancelInput {
    private String paymentKey;
    private String cancelReason;
}
