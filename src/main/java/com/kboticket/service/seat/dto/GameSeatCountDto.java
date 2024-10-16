package com.kboticket.service.seat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameSeatCountDto implements SeatCount{
    private String level;
    private String block;
    private Long count;
}