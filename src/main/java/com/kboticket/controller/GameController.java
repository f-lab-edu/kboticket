package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.common.constants.ResponseCode;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import com.kboticket.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    @GetMapping("/list")
    public CommonResponse<List<GameResponse>> list(@RequestBody GameSearchDto gameSearchDto,
                                                   @RequestParam(value = "cursor", required = false) Long cursor,
                                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<GameResponse> gameList = gameService.getGameList(pageable, gameSearchDto, cursor, limit);

        return new CommonResponse<>(
                ResponseCode.SUCCESS, null, gameList);
    }
}