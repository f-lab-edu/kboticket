package com.kboticket.controller.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class GameSearchRequest {

    private String[] teams;
    private String homeTeam;
    private String awayTeam;
    private String startDate;
    private String endDate;
    private String stadium;

}
