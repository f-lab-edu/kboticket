package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import com.kboticket.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameSrchController {

    private final GameService gameService;

    @GetMapping("/list")
    public CommonResponse<List<GameResponse>> list(@RequestBody GameSearchDto gameSearchDto,
                                                   @RequestParam(value = "cursor", required = false) String cursor,
                                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {

        // game pk id
        // 스크롤 pageable 이용안해도됨 -> 쿼리 두번발생, 전체 문서갯수
        Pageable pageable = PageRequest.of(0, limit);
        Slice<GameResponse> gameList = gameService.getGameList(pageable, gameSearchDto, cursor);

        return new CommonResponse(gameList);
    }
}
