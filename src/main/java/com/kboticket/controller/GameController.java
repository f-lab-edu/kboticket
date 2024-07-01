package com.kboticket.controller;

import com.kboticket.dto.GameDto;
import com.kboticket.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<GameDto>> list() {
        List<GameDto> gameList = gameService.findGames()
                .stream()
                .map(GameDto::new)
                .toList();

        return ResponseEntity.ok(gameList);
    }
}