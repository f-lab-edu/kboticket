package com.kboticket.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameResponse {

    private Long id;
    private String homeTeam;
    private String awayTeam;
    private String gameDate;
    private String startTime;
    private String stadium;

    @Builder
    public GameResponse(Long id, String homeTeam, String awayTeam, String stadium, String gameDate, String startTime) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.gameDate = gameDate;
        this.stadium = stadium;
        this.startTime = startTime;
    }
}
