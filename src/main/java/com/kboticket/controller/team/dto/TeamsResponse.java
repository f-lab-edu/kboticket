package com.kboticket.controller.team.dto;

import com.kboticket.dto.TeamDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamsResponse {
    private List<TeamDto> teams;
}
