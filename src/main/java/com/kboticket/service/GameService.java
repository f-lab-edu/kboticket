package com.kboticket.service;

import com.kboticket.domain.Game;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import com.kboticket.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Slice<GameResponse> getGameList(Pageable pageable, GameSearchDto gameSearchDto, String cursor) {

        return gameRepository.getByCursor(pageable, gameSearchDto, cursor);

    }

    public GameResponse findById(Long gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        Game game = optionalGame.get();

        GameResponse gameResponse =  GameResponse.builder()
                .id(game.getId())
                .homeTeam(game.getHomeTeam().getName())
                .awayTeam(game.getAwayTeam().getName())
                .gameDate(game.getGameDate())
                .startTime(game.getStartTime())
                .stadium(game.getStadium().getName()).build();

        return gameResponse;

    }
}
