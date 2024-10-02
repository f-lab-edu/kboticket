package com.kboticket.service.game.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameDetailDto {
    private Long id;
    private String homeTeam;
    private String awayTeam;
    private String stadium;
    private String gameDate;
    private String startTime;
    private String gameDay;
    private String gameStatus;      // 경기 상태(SCHEDULED/OPEN/CLOSE)
    private LocalDate openDate;        // 티켓 오픈 일자 (경기일 - 7)
}
