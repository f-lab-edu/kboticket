package com.kboticket.dto.response;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GameResponse {

    private Long id;
    private String homeTeam;
    private String awayTeam;
    private String gameDate;
    private String startTime;
    private String stadium;

}
