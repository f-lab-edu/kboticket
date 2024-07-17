package com.kboticket.repository;

import com.kboticket.domain.Game;
import com.kboticket.domain.QGame;
import com.kboticket.domain.QStadium;
import com.kboticket.domain.QTeam;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
    public Slice<GameResponse> getByCursor(Pageable pageable, GameSearchDto gameSearchDto, String cursor) {

        BooleanBuilder builder = createSearchBuilder(gameSearchDto);

        if (cursor!= null) {
            // 수정
            // 이렇게 사용하려면 클래스를 만들어야함
            String cursorDate = cursor.split("_")[0];
            Long cursorId = Long.parseLong(cursor.split("_")[1]);

            builder.and(game.gameDate.eq(cursorDate).and(game.id.gt(cursorId)));
        }

        List<Game> gameList = queryFactory
                                .select(game)
                                .from(game)
                                .leftJoin(game.homeTeam, homeTeam)
                                .leftJoin(game.awayTeam, awayTeam)
                                .leftJoin(game.stadium, stadium)
                                .where(builder)
                                .orderBy(game.gameDate.asc(), game.id.asc(), homeTeam.name.asc(),awayTeam.name.asc())
                                .limit(pageable.getPageSize() + 1)
                                .fetch();

        return createResponse(pageable, gameList);
    }

    private SliceImpl<GameResponse> createResponse(Pageable pageable, List<Game> gameList) {
        List<GameResponse> responseList = gameList.stream()
                .map(data -> GameResponse.builder()
                    .id(data.getId())
                    .homeTeam(data.getHomeTeam().getName())
                    .awayTeam(data.getAwayTeam().getName())
                    .stadium(data.getStadium().getName())
                    .gameDate(data.getGameDate())
                    .build())
                .collect(Collectors.toList());

        boolean hasNext = ishasNext(pageable, responseList);

        return new SliceImpl<>(responseList, pageable, hasNext);
    }

    private boolean ishasNext(Pageable pageable, List<GameResponse> responseList) {
        boolean hasNext = false;

        if (responseList.size() > pageable.getPageSize()) {
            responseList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return hasNext;
    }

    private BooleanBuilder createSearchBuilder(GameSearchDto gameSearchDto) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(homeTeamLike(gameSearchDto.getHomeTeam()))
                .and(awayTeamLike(gameSearchDto.getAwayTeam()))
                .and(stadiumEq(gameSearchDto.getStadium()))
                .and(gameDateBetween(gameSearchDto.getStartDate(), gameSearchDto.getEndDate()));
        return builder;
    }

    private BooleanExpression homeTeamLike(String homeTeamStr) {
        // 팀 검색 dropbox 로 명확하게 검색
        // like 되게 느림
        // 유저쪽에서 쓰는 건 문제가 됨, 관리자쪽은 괜춘
        // full scan, team -> enum으로
        // 팀 여러개 -> or
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