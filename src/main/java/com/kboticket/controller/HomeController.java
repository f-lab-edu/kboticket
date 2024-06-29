package com.kboticket.controller;

import com.kboticket.dto.GameDto;
import com.kboticket.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final GameService gameService;

    @GetMapping("/")
    public String Home(Model model) {

        List<GameDto> gameList = gameService.findGames()
                .stream()
                .map(GameDto::new)
                .toList();

        model.addAttribute("gameList", gameList);

        return "home";
    }

}
