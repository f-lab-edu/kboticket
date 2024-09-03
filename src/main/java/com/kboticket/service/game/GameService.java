package com.kboticket.service.game;

import com.kboticket.domain.Game;
import com.kboticket.dto.game.GameDto;
import com.kboticket.dto.game.GameSearchDto;
import com.kboticket.dto.seat.SeatCountDto;
import com.kboticket.dto.seat.SeatDto;
import com.kboticket.dto.response.GameResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.game.GameRepository;
import com.kboticket.repository.SeatRepository;
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

    public GameDto getGameList(GameSearchDto gameSearchDto, String cursor, int limit) {

        List<GameResponse> games = gameRepository.getByCursor(gameSearchDto, cursor, limit);

        // hasnext 필요없음
        boolean hasNext = false;

        if (games.size() > limit) {
            hasNext = true;
        }

        GameDto result = GameDto.builder()
                .games(games)
                .hasNext(hasNext)
                .build();

        return result;
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

    public List<SeatCountDto> getSeatLevelAndCounts(Long gameId) {
       // 등급별 예약 가능한 좌석 수
        List<SeatCountDto> seatCounts = seatRepository.findSeatLevelsAndCounts(gameId);

        return seatCounts;

    }

    public List<SeatCountDto> getSeatBlockAndCounts(Long gameId, String level) {
        // 블록별 예약 가능한 좌석 수
        List<SeatCountDto> seatCounts = seatRepository.findSeatBlocksAndCounts(gameId, level);

        return seatCounts;
    }

    public List<SeatDto> getAvailavleSeat(Long gameId, String level, String block) {

        List<SeatDto> seatInfos = seatRepository.findAvailableSeats(gameId, level, block);

        return seatInfos;

    }

    public Game getGame(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> {
           throw new KboTicketException(ErrorCode.NOT_FOUND_GAME);
        });
    }
}