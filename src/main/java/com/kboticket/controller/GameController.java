package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.GameDto;
import com.kboticket.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @GetMapping("/list")
    public CommonResponse<List<GameDto>> list() {
        List<GameDto> gameList = gameService.findGames()
                .stream()
                .map(GameDto::new)
                .toList();

        return new CommonResponse<>(
                0, "gamelist Success", gameList);
    }
}