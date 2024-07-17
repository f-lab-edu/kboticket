package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.SeatDto;
import com.kboticket.dto.StadiumDto;
import com.kboticket.dto.response.SeatsResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.SeatService;
import com.kboticket.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stadium")
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;
    private final SeatService seatService;


    @GetMapping("/view")
    public CommonResponse<StadiumDto> view(@RequestParam String stadiumId,
                                           @RequestParam String gameId) {
        StadiumDto stadium = stadiumService.view(stadiumId);
        List<SeatDto> seat = seatService.getSeatsByStadium(stadiumId);
        stadium.setSeatList(seat);

        return new CommonResponse<>(stadium);
    }

    /**
     * 특정 구단 홈 경기장 정보 (좌석별 요금)
     */
    @GetMapping("/{stadiumId}/seats-price")
    public CommonResponse<SeatsResponse> getSeatsPrice(@PathVariable String stadiumId) {

        boolean isExistsStadiumId = stadiumService.isExistsId(stadiumId);
        if (!isExistsStadiumId) {
            throw  new KboTicketException(ErrorCode.NOT_FOUND_STADIUM);
        }

        List<SeatDto> seatDtos =  seatService.getSeatsPriceByStadium(stadiumId);

        return new CommonResponse<>(new SeatsResponse(seatDtos));
    }


    /**
     * 특정 구단 홈 경기장 정보 (좌석 위치 및 번호)
     */
    @GetMapping("/{stadiumId}/seats-location")
    public CommonResponse<SeatsResponse> getSeatsInfo(@PathVariable String stadiumId) {

        boolean isExistsStadiumId = stadiumService.isExistsId(stadiumId);
        if (!isExistsStadiumId) {
            throw  new KboTicketException(ErrorCode.NOT_FOUND_STADIUM);
        }

        List<SeatDto> seatDtos =  seatService.getSeatsLocationByStadium(stadiumId);

        return new CommonResponse<>(new SeatsResponse(seatDtos));
    }
}
