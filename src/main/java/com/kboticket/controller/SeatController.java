package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.common.constants.ResponseCode;
import com.kboticket.domain.Seat;
import com.kboticket.domain.Stadium;
import com.kboticket.dto.SeatDto;
import com.kboticket.dto.StadiumDto;
import com.kboticket.service.SeatService;
import com.kboticket.service.StadiumService;
import com.kboticket.util.SeatArrangement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

    private final StadiumService stadiumService;
    private final SeatService seatService;

    /**
     * 특정 경기장의 좌석 목록
     */
    @GetMapping("/list")
    public CommonResponse<List<Seat>> list() {
        List<Seat> Seats = seatService.findAll();
        return new CommonResponse<>(Seats);
    }

    /**
     * 예약 가능한 좌석 목록
     */
    @GetMapping("/available")
    public CommonResponse<StadiumDto> getAvailableList(@RequestParam String stadiumId,
                                                       @RequestParam Long gameId) {
        StadiumDto stadium = stadiumService.view(stadiumId);
        List<SeatDto> availableSeats = seatService.getAvailableSeats(stadiumId, gameId);
        stadium.setSeatList(availableSeats);

        return new CommonResponse<>(stadium);
    }

    /**
     * 좌석 정보 조회
     */
    @GetMapping("/{seatId}")
    public CommonResponse<SeatDto> getTicketInfo(@RequestParam Long seatId) {

        SeatDto seatDto = seatService.getSeatInfo(seatId);

        return new CommonResponse(seatDto);
    }

    /**
     * 좌석 예약
     */
    @PostMapping("/seat/reserve")
    public void reserveSeat(@RequestParam Long[] seatIds,
                            @RequestParam Long userId,
                            @RequestParam Long gameId) {
        seatService.selectSeats(seatIds, userId, gameId);

    }

    @GetMapping("/save")
    public void saveSeats() {
        Stadium stadium = new Stadium();
        String[] id = {"DJ", "KJ", "GO", "IN", "DG", "CH", "SE"};
        for (int i=0; i<id.length; i++) {
            stadium.setId(id[i]);
            List<Seat> seatList = SeatArrangement.generateSeats(stadium, 100, 10);
            seatService.saveSeats(seatList);
        }

    }
}
