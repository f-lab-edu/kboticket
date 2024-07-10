package com.kboticket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class GameSearchDto {
    private String homeTeam;
    private String awayTeam;
    private String startDate;
    private String endDate;
    private String stadium;

}
