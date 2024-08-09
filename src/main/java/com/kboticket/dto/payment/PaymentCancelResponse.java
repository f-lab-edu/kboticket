package com.kboticket.dto.payment;

import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCancelResponse {
    private Long cancelAmount;
    private String cancelReson;
    private String taxFreeAmount;
    private Long taxExemptionAmount;
    private Long refundableAmount;
    private Long easyPayDiscountAmount;
    private String canceledAt;
    private String transactionKey;
    private String receiptKey;
    private String cancelStatus;
    private String cancelRequestId;
}
