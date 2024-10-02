package com.kboticket.service.game;

import com.kboticket.controller.game.dto.GameDetailResponse;
import com.kboticket.controller.game.dto.GameSearchRequest;
import com.kboticket.controller.game.dto.GameSearchResponse;
import com.kboticket.domain.Game;
import com.kboticket.repository.game.GameRepository;
import com.kboticket.service.game.dto.GameDetailDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public GameSearchResponse getGameList(GameSearchRequest gameSearchRequest, String cursorId, int limit) {
        List<GameDetailResponse> games = gameRepository.getByCursor(gameSearchRequest, cursorId, limit);

        boolean hasNext = false;
        if (games.size() > limit) {
            hasNext = true;
        }

        return GameSearchResponse.builder()
                .games(games)
                .hasNext(hasNext)
                .build();
    }

    public GameDetailDto findById(Long gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);

        Game game = optionalGame.get();
        GameDetailDto gameDetailDto =  GameDetailDto.builder()
                .id(game.getId())
                .homeTeam(game.getHomeTeam().getName())
                .awayTeam(game.getAwayTeam().getName())
                .gameDate(game.getGameDate())
                .startTime(game.getStartTime())
                .stadium(game.getStadium().getName()).build();

        return gameDetailDto;
    }
}
