package com.kboticket.repository.game;

import com.kboticket.controller.game.dto.GameDetailResponse;
import com.kboticket.controller.game.dto.GameSearchRequest;
import com.kboticket.domain.Game;
import com.kboticket.domain.QGame;
import com.kboticket.domain.QStadium;
import com.kboticket.domain.QTeam;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@AllArgsConstructor
public class GameCustomRepositoryImpl implements GameCustomRepository{

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory queryFactory;
    private static final QGame game = QGame.game;
    private static final QTeam homeTeam = new QTeam("homeTeam");
    private static final QTeam awayTeam = new QTeam("awayTeam");
    private static final QStadium stadium = QStadium.stadium;

    @Override
    public List<GameDetailResponse> getByCursor(GameSearchRequest gameSearchRequest, String cursorId, int limit) {
        BooleanBuilder builder = createSearchBuilder(gameSearchRequest);

        if (cursorId != null) {
            builder.and(game.id.gt(Long.parseLong(cursorId)));
        }

        List<Game> gameList = queryFactory
                                .select(game)
                                .from(game)
                                .leftJoin(game.homeTeam, homeTeam)
                                .leftJoin(game.awayTeam, awayTeam)
                                .leftJoin(game.stadium, stadium)
                                .where(builder)
                                .orderBy(game.gameDate.asc(), game.id.asc(), homeTeam.name.asc())
                                .limit(limit)
                                .fetch();

        return createResponse(gameList);
    }

    private List<GameDetailResponse> createResponse(List<Game> gameList) {
        List<GameDetailResponse> responseList = gameList.stream()
                .map(data -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate date = LocalDate.parse(data.getGameDate(), formatter);

                    LocalDate openDate = date.minusDays(7);
                    LocalDateTime openDateTime = openDate.atTime(11, 00, 00);

                    String format = openDateTime.getHour() + ":" + String.format("%02d", openDateTime.getMinute());

                    String dayOfWeek = openDate.getDayOfWeek().toString();

                    return GameDetailResponse.builder()
                        .id(data.getId())
                        .homeTeam(data.getHomeTeam().getId())
                        .awayTeam(data.getAwayTeam().getId())
                        .stadium(data.getStadium().getId())
                        .gameDate(data.getGameDate())
                        .startTime(data.getStartTime())
                        .openDate(openDate)
                        .openTime(format)
                        .gameStatus("SCHEDULED")
                        .gameDay(dayOfWeek)
                        .build();

                    }
                )
                .collect(Collectors.toList());

        return responseList;
    }

    private BooleanBuilder createSearchBuilder(GameSearchRequest request) {
        return new BooleanBuilder()
            .and(teamsEq(request.getTeam()))
            .and(stadiumEq(request.getStadium()))
            .and(gameDateFilter(request.getMonth()));
    }

    private BooleanExpression teamsEq(String team) {
        return team == null ? null : homeTeam.id.eq(team).or(awayTeam.id.eq(team));
    }

    private BooleanExpression stadiumEq(String stadiumStr) {
        return StringUtils.hasText(stadiumStr) ? stadium.id.eq(stadiumStr) : null;
    }

    private BooleanExpression gameDateFilter(String searchMonth) {
        LocalDateTime now = LocalDateTime.now();

        String month = (searchMonth != null) ? searchMonth : String.valueOf(now.getMonthValue());

        return game.gameDate.substring(5,7).eq(month);
    }
}