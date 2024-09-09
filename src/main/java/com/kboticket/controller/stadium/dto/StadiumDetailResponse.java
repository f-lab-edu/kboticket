package com.kboticket.controller.stadium.dto;

import com.kboticket.service.seat.dto.SeatDto;
import com.kboticket.service.stadium.dto.StadiumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter @Setter
@AllArgsConstructor
public class StadiumDetailResponse {

    private StadiumDto stadium;
    private List<SeatDto> seats;

    public static StadiumDetailResponse of(StadiumDto stadium, List<SeatDto> seats) {
        return StadiumDetailResponse.builder()
                .stadium(stadium)
                .seats(seats)
                .build();
    }
}
