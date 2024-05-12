package com.kboticket.service;

import com.kboticket.domain.Game;
import com.kboticket.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public List<Game> findGames() {
        return gameRepository.findAll();
    }

}
