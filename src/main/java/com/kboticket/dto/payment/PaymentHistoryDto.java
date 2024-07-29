package com.kboticket.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryDto {

    private Long historyId;
    private Long amount;
    private Long orderName;
    private LocalDateTime createdAt;
}
