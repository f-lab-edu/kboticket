package com.kboticket.controller.game;

import com.kboticket.common.CommonResponse;
import com.kboticket.controller.QueueService;
import com.kboticket.controller.game.dto.GameDetailResponse;
import com.kboticket.controller.game.dto.GameSearchRequest;
import com.kboticket.controller.game.dto.GameSearchResponse;
import com.kboticket.service.game.GameService;
import com.kboticket.service.game.dto.GameDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    private final QueueService queueService;

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
     * 경기 예매 상세 화면
     */
    @GetMapping("/{gameId}")
    public CommonResponse<GameDetailResponse> view(@PathVariable Long gameId) {

        GameDetailDto gameDetailDto = gameService.findById(gameId);
        GameDetailResponse response = GameDetailResponse.from(gameDetailDto);

        return new CommonResponse<>(response);
    }

    /**
     * 실시간 순번 조회 및 경기 좌석 예매 화면 진입
     */
    @GetMapping(value = "/queue-status/{gameId}", produces = "text/event-stream")
    public SseEmitter getQueueStatus(@PathVariable Long gameId, String email) {
        SseEmitter sseEmitter = queueService.createEmitter(email);

        queueService.sendEvents();

        return sseEmitter;
    }
}
