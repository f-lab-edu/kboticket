package com.kboticket.dto.payment;

import com.kboticket.domain.User;
import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private User user;
    private boolean paySuccessYn;

}
