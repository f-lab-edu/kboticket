package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.GameDto;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameSearchController {

    private final GameService gameService;

    @GetMapping("/search")
    public CommonResponse<GameDto> list(@RequestBody GameSearchDto gameSearchDto,
                                        @RequestParam(value = "cursor", required = false) String cursor,
                                        @RequestParam(value = "limit", defaultValue = "10") int limit) {
        GameDto gameList = gameService.getGameList(gameSearchDto, cursor, limit);

        return new CommonResponse<>(gameList);
    }
}
