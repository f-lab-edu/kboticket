package com.kboticket.controller.seat.dto;

import com.kboticket.service.seat.dto.SeatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SeatsResponse {

    private List<SeatDto> seats;

}
