package com.kboticket.controller.game;

import com.kboticket.common.CommonResponse;
import com.kboticket.controller.game.dto.GameSearchResponse;
import com.kboticket.controller.game.dto.GameSearchRequest;
import com.kboticket.controller.game.dto.GameDetailResponse;
import com.kboticket.service.game.GameService;
import com.kboticket.service.game.dto.GameDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * 경기 목록 조회
     */
    @GetMapping("/list")
    public CommonResponse<GameSearchResponse> list(@RequestBody GameSearchRequest gameSearchRequest,
                                                   @RequestParam(value = "cursor", required = false) String cursorId,
                                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {
        GameSearchResponse gameList = gameService.getGameList(gameSearchRequest, cursorId, limit);

        return new CommonResponse<>(gameList);
    }

    /**
     * 경기 상세
     */
    @GetMapping("/{gameId}")
    public CommonResponse<GameDetailResponse> view(@PathVariable Long gameId) {
        GameDetailDto gameDetailDto = gameService.findById(gameId);

        GameDetailResponse response = GameDetailResponse.from(gameDetailDto);

        return new CommonResponse<>(response);
    }

}
