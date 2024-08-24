package com.kboticket.repository.game;

import com.kboticket.domain.Game;
import com.kboticket.domain.QGame;
import com.kboticket.domain.QStadium;
import com.kboticket.domain.QTeam;
import com.kboticket.dto.game.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<GameResponse> getByCursor(GameSearchDto gameSearchDto, String cursor, int limit) {

        BooleanBuilder builder = createSearchBuilder(gameSearchDto);

        // gameId 를 넘겨준다
        if (cursor!= null) {
            builder.and(game.id.gt(Long.parseLong(cursor)));
        }

        List<Game> gameList = queryFactory
                                .select(game)
                                .from(game)
                                .leftJoin(game.homeTeam, homeTeam)
                                .leftJoin(game.awayTeam, awayTeam)
                                .leftJoin(game.stadium, stadium)
                                .where(builder)
                                .orderBy(game.gameDate.asc(), game.id.asc(), homeTeam.name.asc())
                                .limit(limit + 1)
                                .fetch();

        return createResponse(gameList);
    }

    private List<GameResponse> createResponse(List<Game> gameList) {
        List<GameResponse> responseList = gameList.stream()
                .map(data -> GameResponse.builder()
                    .id(data.getId())
                    .homeTeam(data.getHomeTeam().getId())
                    .awayTeam(data.getAwayTeam().getId())
                    .stadium(data.getStadium().getId())
                    .gameDate(data.getGameDate())
                    .build())
                .collect(Collectors.toList());

        return responseList;
    }

    private BooleanBuilder createSearchBuilder(GameSearchDto gameSearchDto) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(teamsIn(gameSearchDto.getTeams()))
                .and(stadiumEq(gameSearchDto.getStadium()))
                .and(gameDateBetween(gameSearchDto.getStartDate(), gameSearchDto.getEndDate())
                .and(gameDateInFuture()));
        return builder;
    }

    private Predicate gameDateInFuture() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeTemplate<LocalDateTime> gameDateAsLocalDateTime = Expressions.dateTimeTemplate(LocalDateTime.class,
                "STR_TO_DATE({0}, '%Y.%m.%d')", game.gameDate);
        return gameDateAsLocalDateTime.goe(now);
    }

    private BooleanExpression teamsIn(String[] teams) {
        if (teams == null) {
            return null;
        }
        return homeTeam.id.in(teams).or(awayTeam.id.in(teams));
    }


    private BooleanExpression stadiumEq(String stadiumStr) {
        return StringUtils.hasText(stadiumStr) ? stadium.id.eq(stadiumStr) : null;
    }

    private BooleanExpression gameDateBetween(String srchStartDt, String srchEndDt) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        LocalDateTime now = LocalDateTime.now();

        if (srchStartDt == null) {
            // startDate가 null인 경우, 현재 날짜를 시작 날짜로 설정
            startDate = now;
        } else {
            startDate = parseToLocalDateTime(srchStartDt);
            if (startDate.isBefore(now)) {
                throw new KboTicketException(ErrorCode.INVALID_START_DATE);
            }
        }

        if (srchEndDt != null) {
            endDate = parseToLocalDateTime(srchEndDt);
        }

        DateTimeTemplate<LocalDateTime> gameDateAsLocalDateTime = Expressions.dateTimeTemplate(LocalDateTime.class,
                "STR_TO_DATE({0}, '%Y.%m.%d')", game.gameDate);

        if (startDate != null && endDate != null) {
            return gameDateAsLocalDateTime.between(startDate, endDate);
        } else if (startDate != null) {
            return gameDateAsLocalDateTime.goe(startDate);
        } else if (endDate != null) {
            return gameDateAsLocalDateTime.loe(endDate);
        } else {
            return null;
        }
    }

    public static LocalDateTime parseToLocalDateTime(String gameDateString) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String DateString = gameDateString.split("\\(")[0];

        try {
            LocalDate localDate = LocalDate.parse(DateString, dateFormatter);
            return localDate.atStartOfDay();
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}