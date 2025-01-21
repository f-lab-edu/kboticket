package com.kboticket.controller.game;

import com.kboticket.common.CommonResponse;
import com.kboticket.controller.QueueService;
import com.kboticket.controller.game.dto.GameDetailResponse;
import com.kboticket.controller.game.dto.GameSearchRequest;
import com.kboticket.controller.game.dto.GameSearchResponse;
import com.kboticket.service.game.GameService;
import com.kboticket.service.game.dto.GameDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final QueueService queueService;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 경기 목록 조회
     */
    @GetMapping("/list")
    public CommonResponse<GameSearchResponse> list( @RequestParam(value = "cursor", required = false) String cursorId,
        @RequestParam(value = "limit", defaultValue = "10") int limit,
        @RequestParam(value = "team", required = false) String team,
        @RequestParam(value = "stadium", required = false) String stadium,
        @RequestParam(value = "month", required = true) String month,
        @RequestParam(value = "dayOfMonth", required = true) String dayOfMonth) {

        GameSearchRequest request = GameSearchRequest.builder()
            .team(team)
            .stadium(stadium)
            .month(month)
            .dayOfMonth(dayOfMonth)
            .build();

        String gameKey = String.format("%s-%s", month, dayOfMonth);

        GameSearchResponse gameList = gameService.getGameList(request, gameKey, cursorId, limit);

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
    public SseEmitter getQueueStatus(@PathVariable Long gameId, Authentication authentication) {
        String email = authentication.getName();
        SseEmitter sseEmitter = queueService.createEmitter(email);

        queueService.sendEvents();

//        sseEmitter.onCompletion(() -> {
//            redisTemplate.opsForZSet().remove("ticketing-queue", email);
//        });
//
//        sseEmitter.onError((e) -> {
//            redisTemplate.opsForZSet().remove("ticketing-queue", email);
//        });

        return sseEmitter;
    }
}
