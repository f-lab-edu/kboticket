package com.kboticket.dto.payment;

import com.kboticket.domain.Payment;
import com.kboticket.domain.User;
import com.kboticket.enums.PaymentStatus;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private User user;
    private String cancelReason;
    private String failReason;
    private PaymentStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private boolean paySuccessYn;


    public static PaymentDto from(Payment payment) {
        return PaymentDto.builder()
                .paymentKey(payment.getPaymentKey())
                .amount(payment.getAmount())
                .cancelReason(payment.getCancelReason())
                .failReason(payment.getFailReason())
                .status(payment.getStatus())
                .requestedAt(payment.getRequestedAt())
                .approvedAt(payment.getApprovedAt())
                .build();
    }
}
