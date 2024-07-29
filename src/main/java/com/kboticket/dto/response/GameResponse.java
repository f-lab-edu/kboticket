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
    // 약관, 요일, 팀을 클래스로, boolean 예매 오픈되었는지notnull, false인경우 시간 추가nullable, 딱지 boolean,

}

// 홈팀에 정보가 더 있으면 클래스로 , 홈팀, 이미지 url
// enum 으로 lg