package com.kboticket.controller.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class GameSearchRequest {

    private String team;
    private String stadium;
    private String month;
    private String dayOfMonth;

}
