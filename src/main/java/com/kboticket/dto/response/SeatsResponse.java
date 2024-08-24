package com.kboticket.dto.response;

import com.kboticket.dto.seat.SeatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SeatsResponse {

    private List<SeatDto> seats;

}
