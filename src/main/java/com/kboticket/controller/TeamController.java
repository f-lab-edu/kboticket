package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.TeamDto;
import com.kboticket.dto.response.TeamsResponse;
import com.kboticket.enums.TeamEnum;
import com.kboticket.service.TeamServie;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    /**
     * 팀 리스트 조회
     */
    @GetMapping
    public CommonResponse<TeamsResponse> list() {
        List<TeamDto> teamDtoList = new ArrayList<>();
        for (TeamEnum team : TeamEnum.values()) {

            TeamDto teamDto = TeamDto.builder()
                    .code(team.code)
                    .name(team.name)
                    .stadiumCode(team.stadiumCode)
                    .build();

            teamDtoList.add(teamDto);
        }

        TeamsResponse response = new TeamsResponse(teamDtoList);
        return new CommonResponse<>(response);
    }
}
