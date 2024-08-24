package com.kboticket.dto;

import com.kboticket.dto.seat.SeatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter @Setter
@AllArgsConstructor
public class StadiumDto {

    private String name;

    private String address;

    private List<SeatDto> seatList;
}
