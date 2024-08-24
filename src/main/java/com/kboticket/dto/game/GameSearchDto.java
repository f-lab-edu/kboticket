package com.kboticket.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class GameSearchDto {

    private String[] teams;
    private String homeTeam;
    private String awayTeam;
    private String startDate;
    private String endDate;
    private String stadium;

}
