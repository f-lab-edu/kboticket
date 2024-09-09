package com.kboticket.service.game.dto;

import com.kboticket.service.seat.dto.SeatDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AvailableSeatResponse {
    private List<SeatDto> seats;

    public static AvailableSeatResponse from(List<SeatDto> seatDtos) {
        return AvailableSeatResponse.builder()
                .seats(seatDtos)
                .build();
    }
}
