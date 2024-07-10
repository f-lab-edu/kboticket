package com.kboticket.repository;

import com.kboticket.domain.Game;
import com.kboticket.domain.QGame;
import com.kboticket.domain.QStadium;
import com.kboticket.domain.QTeam;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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
    public List<GameResponse> getByCursor(Pageable pageable, GameSearchDto gameSearchDto, Long cursor, int limit) {

        List<Game> gameList = queryFactory
                                .select(game)
                                .from(game)
                                .leftJoin(game.homeTeam, homeTeam)
                                .leftJoin(game.awayTeam, awayTeam)
                                .leftJoin(game.stadium, stadium)
                                .where(homeTeamLike(gameSearchDto.getHomeTeam()),
                                       awayTeamLike(gameSearchDto.getAwayTeam()),
                                       stadiumEq(gameSearchDto.getStadium()),
                                       gameDateBetween(gameSearchDto.getStartDate(), gameSearchDto.getEndDate())
                                )
                                .limit(limit)
                                .fetch();

        List<GameResponse> responseList = new ArrayList<>();

        for (Game data : gameList) {
            GameResponse gameResponse = GameResponse.builder()
                    .id(data.getId())
                    .homeTeam(data.getHomeTeam().getName())
                    .awayTeam(data.getAwayTeam().getName())
                    .stadium(data.getStadium().getName())
                    .gameDate(data.getGameDate())
                    .build();

            responseList.add(gameResponse);
        }
        return responseList;
    }

    private BooleanExpression homeTeamLike(String homeTeamStr) {
        return StringUtils.hasText(homeTeamStr) ? homeTeam.name.like("%" + homeTeamStr + "%") : null;
    }

    private BooleanExpression awayTeamLike(String awayTeamStr) {
        return StringUtils.hasText(awayTeamStr) ? awayTeam.name.like("%" +awayTeamStr + "%") : null;
    }

    private BooleanExpression stadiumEq(String stadiumStr) {
        return StringUtils.hasText(stadiumStr) ? stadium.name.eq(stadiumStr) : null;
    }

    private BooleanExpression gameDateBetween(String startDt, String endDt) {

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (startDt != null) {
            startDate = parseToLocalDateTime(startDt);
        }
        if (endDt != null) {
            endDate = parseToLocalDateTime(endDt);
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