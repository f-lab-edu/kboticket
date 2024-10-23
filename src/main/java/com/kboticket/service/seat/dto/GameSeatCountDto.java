package com.kboticket.service.seat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameSeatCountDto implements SeatCount{
    private String level;
    private Long block;
    private Long count;

    public GameSeatCountDto(String level, Long block) {
        this.level = level;
        this.block = block;
    }
}