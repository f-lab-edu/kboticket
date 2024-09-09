package com.kboticket.controller.game.dto;

import com.kboticket.service.game.dto.GameDetailDto;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GameDetailResponse {

    private Long id;
    private String homeTeam;
    private String awayTeam;
    private String gameDate;
    private String startTime;
    private String stadium;
    // 약관, 요일, 팀을 클래스로, boolean 예매 오픈되었는지notnull, false인경우 시간 추가nullable, 딱지 boolean,

    public static GameDetailResponse from (GameDetailDto detailDto) {
        return GameDetailResponse.builder()
                .homeTeam(detailDto.getHomeTeam())
                .awayTeam(detailDto.getAwayTeam())
                .gameDate(detailDto.getGameDate())
                .startTime(detailDto.getStartTime())
                .stadium(detailDto.getStadium())
                .build();
    }
}

// 홈팀에 정보가 더 있으면 클래스로 , 홈팀, 이미지 url
// enum 으로 lg