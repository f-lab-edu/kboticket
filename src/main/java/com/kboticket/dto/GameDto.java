package com.kboticket.dto;

import com.kboticket.domain.Game;
import com.kboticket.domain.Stadium;
import com.kboticket.domain.Team;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class GameDto {
    private String gameDate;
    private String startTime;
    private String gameInfo;
    private String homeTeamNm;
    private String awayTeamNm;
    private String stadiumNm;

    public GameDto(Game g) {
        this.gameDate = g.getGameDate();
        this.startTime = g.getStartTime();
        this.gameInfo = g.getGameInfo();
        this.stadiumNm = g.getStadium().getName();
        this.homeTeamNm = g.getHomeTeam().getName();
        this.awayTeamNm = g.getAwayTeam().getName();
    }





}
