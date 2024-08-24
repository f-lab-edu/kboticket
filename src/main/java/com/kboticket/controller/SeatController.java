package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.domain.Stadium;
import com.kboticket.dto.seat.SeatDto;
import com.kboticket.enums.StadiumInfo;
import com.kboticket.service.SeatService;
import com.kboticket.util.SeatArrangement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    /**
     * 좌석 정보 조회
     */
    @GetMapping("/{seatId}")
    public CommonResponse<SeatDto> getTicketInfo(@RequestParam Long seatId) {

        SeatDto seatDto = seatService.getSeatInfo(seatId);

        return new CommonResponse(seatDto);
    }

    /**
     * 경기장 좌석 세팅
     */
    @GetMapping("/save")
    public void saveSeats() {
        Arrays.stream(StadiumInfo.values())
                .map(stadiumInfo -> {
                    Stadium stadium = new Stadium();
                    stadium.setId(stadiumInfo.code);
                    return SeatArrangement.generateSeats(stadium);
                })
                .forEach(seatService::saveSeats);

    }
}
