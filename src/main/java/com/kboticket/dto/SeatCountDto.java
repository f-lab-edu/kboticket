package com.kboticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatCountDto implements SeatCount{
    private String level;
    private String block;
    private Long count;
}