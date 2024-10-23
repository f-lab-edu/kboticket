package com.kboticket.controller.seat.dto;

import com.kboticket.domain.Game;
import com.kboticket.service.seat.dto.GameSeatCountDto;
import lombok.Builder;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeatResponse {

    private Game game;
    private List<GameSeatCountDto> seats;

    public static SeatResponse from (List<GameSeatCountDto> dtos) {
        return SeatResponse.builder()
                .seats(dtos)
                .build();
    }
}
