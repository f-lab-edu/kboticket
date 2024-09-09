package com.kboticket.service.game;

import com.kboticket.controller.game.dto.GameDetailResponse;
import com.kboticket.controller.game.dto.GameSearchRequest;
import com.kboticket.controller.game.dto.GameSearchResponse;
import com.kboticket.domain.Game;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.SeatRepository;
import com.kboticket.repository.game.GameRepository;
import com.kboticket.service.game.dto.GameDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final SeatRepository seatRepository;

    public GameSearchResponse getGameList(GameSearchRequest gameSearchRequest, String cursor, int limit) {
        List<GameDetailResponse> games = gameRepository.getByCursor(gameSearchRequest, cursor, limit);

        boolean hasNext = false;

        if (games.size() > limit) {
            hasNext = true;
        }

        GameSearchResponse result = GameSearchResponse.builder()
                .games(games)
                .hasNext(hasNext)
                .build();

        return result;
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

    public Game getGame(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> {
           throw new KboTicketException(ErrorCode.NOT_FOUND_GAME);
        });
    }
}
