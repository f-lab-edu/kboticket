package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.SeatCountDto;
import com.kboticket.dto.SeatDto;
import com.kboticket.dto.response.GameResponse;
import com.kboticket.service.GameService;
import com.kboticket.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final ReservationService reservationService;


    @GetMapping("/{gameId}")
    public CommonResponse<GameResponse> view(@PathVariable Long gameId) {
        GameResponse response = gameService.findById(gameId);

        return new CommonResponse<>(response);
    }

    @GetMapping("/{gameId}/seats-level")
    public CommonResponse<List<SeatCountDto>> getSeatsByLevel(@PathVariable Long gameId) {
        List<SeatCountDto> seatCounts = gameService.getSeatLevelAndCounts(gameId);

        return new CommonResponse<>(seatCounts);
    }

    @GetMapping("/{gameId}/seats-block")
    public CommonResponse<List<SeatCountDto>> getSeatsByBlock(@PathVariable Long gameId,
                                                              @RequestParam String level) {
        List<SeatCountDto> seatCounts = gameService.getSeatBlockAndCounts(gameId, level);

        return new CommonResponse<>(seatCounts);
    }

    @GetMapping("/{gameId}/seats-available")
    public CommonResponse<List<SeatDto>> getSeatsAvailable(@PathVariable Long gameId,
                                                             @RequestParam String level,
                                                             @RequestParam String block
                                                             ) {
        List<SeatDto> seatInfos = gameService.getAvailavleSeat(gameId, level, block);

        return new CommonResponse<>(seatInfos);
    }





}
