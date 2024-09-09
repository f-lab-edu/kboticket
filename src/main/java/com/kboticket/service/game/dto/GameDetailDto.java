package com.kboticket.service.game.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameDetailDto {
    private Long id;
    private String homeTeam;
    private String awayTeam;
    private String gameDate;
    private String startTime;
    private String stadium;
    private String gameInfo;

}
