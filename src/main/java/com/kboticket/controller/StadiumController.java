package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.common.constants.ResponseCode;
import com.kboticket.domain.Seat;
import com.kboticket.dto.SeatDto;
import com.kboticket.dto.StadiumDto;
import com.kboticket.service.SeatService;
import com.kboticket.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stadium")
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;
    private final SeatService seatService;


    @GetMapping("/view")
    public CommonResponse<StadiumDto> view(@RequestParam String stadiumId,
                                           @RequestParam String gameId) {
        StadiumDto stadium = stadiumService.view(stadiumId);
        List<SeatDto> seat = seatService.getSeatsByStadium(stadiumId);
        stadium.setSeatList(seat);

        return new CommonResponse<>( stadium);
    }
}
