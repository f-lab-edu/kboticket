package com.kboticket.controller.game.dto;

import com.kboticket.service.game.dto.GameDetailDto;
import java.time.LocalDate;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GameDetailResponse {

    private Long id;
    private String homeTeam;
    private String awayTeam;
    private String stadium;
    private String gameDate;
    private String startTime;
    private String gameDay;
    private String gameStatus;      // 경기 상태 (SCHEDULED/OPEN/CLOSE)
    private LocalDate openDate;     // 티켓 오픈 일자 (경기일 - 7)
    private String openTime;        // 티켓 오픈 시간 (11:00)

    public static GameDetailResponse from (GameDetailDto dto) {
        return GameDetailResponse.builder()
                .homeTeam(dto.getHomeTeam())
                .awayTeam(dto.getAwayTeam())
                .stadium(dto.getStadium())
                .gameDate(dto.getGameDate())
                .startTime(dto.getStartTime())
                .gameDate(dto.getGameDate())
                .gameStatus(dto.getGameStatus())
                .openDate(dto.getOpenDate())
                .build();
    }
}