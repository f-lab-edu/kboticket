package com.kboticket.dto;


import com.kboticket.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservedSeatInfo {
    private Long gameId;
    private Long seatId;
    private String email;
    private ReservationStatus status;
    private LocalDateTime reservedDate;
}
