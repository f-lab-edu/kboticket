package com.kboticket.controller.game.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameSearchResponse {

    private boolean hasNext;
    private List<GameDetailResponse> games;

}
