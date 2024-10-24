package com.kboticket.dto.payment;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelRequest {
    private String paymentKey;
    private String cancelReason;
    private int cancelAmount;
    private String orderId;
    private Long[] ticketId;
}
