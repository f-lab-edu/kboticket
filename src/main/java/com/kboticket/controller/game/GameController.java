package com.kboticket.controller.game;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.game.GameDto;
import com.kboticket.dto.game.GameSearchDto;
import com.kboticket.dto.seat.SeatCountDto;
import com.kboticket.dto.seat.SeatDto;
import com.kboticket.dto.response.GameResponse;
import com.kboticket.service.game.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * 경기 목록 조회
     */
    @GetMapping("/list")
    public CommonResponse<GameDto> list(@RequestBody GameSearchDto gameSearchDto,
                                        @RequestParam(value = "cursor", required = false) String cursor,
                                        @RequestParam(value = "limit", defaultValue = "10") int limit) {
        GameDto gameList = gameService.getGameList(gameSearchDto, cursor, limit);

        return new CommonResponse<>(gameList);
    }

    /**
     * 경기 상세
     */
    @GetMapping("/{gameId}")
    public CommonResponse<GameResponse> view(@PathVariable Long gameId) {
        GameResponse response = gameService.findById(gameId);

        return new CommonResponse<>(response);
    }

    /**
     * 경기 좌석 조회 - 레벨별
     */
    @GetMapping("/{gameId}/seats-level")
    public CommonResponse<List<SeatCountDto>> getSeatsByLevel(@PathVariable Long gameId) {
        List<SeatCountDto> seatCounts = gameService.getSeatLevelAndCounts(gameId);

        return new CommonResponse<>(seatCounts);
    }

    /**
     * 경게 좌석 조회 - 블록별
     */
    @GetMapping("/{gameId}/seats-block")
    public CommonResponse<List<SeatCountDto>> getSeatsByBlock(@PathVariable Long gameId,
                                                              @RequestParam String level) {
        List<SeatCountDto> seatCounts = gameService.getSeatBlockAndCounts(gameId, level);

        return new CommonResponse<>(seatCounts);
    }

    /**
     * 예약 가능 좌석
     */
    @GetMapping("/{gameId}/seats-available")
    public CommonResponse<List<SeatDto>> getSeatsAvailable(@PathVariable Long gameId,
                                                             @RequestParam String level,
                                                             @RequestParam String block) {
        List<SeatDto> seatInfos = gameService.getAvailavleSeat(gameId, level, block);

        return new CommonResponse<>(seatInfos);
    }
}
