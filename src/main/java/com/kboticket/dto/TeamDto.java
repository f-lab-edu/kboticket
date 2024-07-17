package com.kboticket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TeamDto {

    private String code;
    private String name;
    private String stadiumCode;
}
