package com.kboticket.controller.stadium;

import com.kboticket.common.CommonResponse;
import com.kboticket.service.seat.dto.SeatDto;
import com.kboticket.controller.stadium.dto.StadiumDetailResponse;
import com.kboticket.service.seat.SeatService;
import com.kboticket.service.stadium.StadiumService;
import com.kboticket.service.stadium.dto.StadiumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stadium")
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;
    private final SeatService seatService;

    @GetMapping("/view")
    public CommonResponse<StadiumDetailResponse> view(@RequestParam String stadiumId) {
        StadiumDto stadium = stadiumService.view(stadiumId);
        List<SeatDto> seats = seatService.getSeatsByStadium(stadiumId);

        StadiumDetailResponse response = StadiumDetailResponse.of(stadium, seats);

        return new CommonResponse<>(response);
    }
}
