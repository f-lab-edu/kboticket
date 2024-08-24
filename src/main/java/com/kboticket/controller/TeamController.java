package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.TeamDto;
import com.kboticket.dto.response.TeamsResponse;
import com.kboticket.enums.KboTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    /**
     * 팀 목록 조회
     */
    @GetMapping
    public CommonResponse<TeamsResponse> list() {
        List<TeamDto> teamDtos = Stream.of(KboTeam.values())
                .sorted(Comparator.comparing(KboTeam::getName))
                .map(team -> TeamDto.builder()
                    .code(team.code)
                    .name(team.name)
                    .stadiumCode(team.stadiumCode).build())
                .collect(Collectors.toList());

        TeamsResponse response = new TeamsResponse(teamDtos);
        return new CommonResponse<>(response);
    }
}
