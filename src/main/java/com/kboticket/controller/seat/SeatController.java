package com.kboticket.controller.seat;

import com.kboticket.common.CommonResponse;
import com.kboticket.controller.seat.dto.SeatResponse;
import com.kboticket.controller.seat.dto.SeatsResponse;
import com.kboticket.domain.Stadium;
import com.kboticket.service.seat.dto.GameSeatCountDto;
import com.kboticket.service.seat.dto.SeatDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.StadiumInfo;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.game.dto.AvailableSeatResponse;
import com.kboticket.service.seat.SeatService;
import com.kboticket.common.util.SeatArrangement;
import com.kboticket.service.stadium.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;
    private final StadiumService stadiumService;

    /**
     * 좌석 정보 조회
     */
    @GetMapping("/{seatId}")
    public CommonResponse<SeatDto> getTicketInfo(@RequestParam Long seatId) {
        SeatDto seatDto = seatService.getSeatInfo(seatId);

        return new CommonResponse(seatDto);
    }


    /**
     * 경기 좌석 조회 - 레벨별
     */
    @GetMapping("/{gameId}/seats-level")
    public CommonResponse<SeatResponse> getSeatsByLevel(@PathVariable Long gameId) {
        List<GameSeatCountDto> seatCountList = seatService.getSeatLevelAndCounts(gameId);

        SeatResponse response = SeatResponse.builder()
                .seats(seatCountList)
                .build();

        return new CommonResponse<>(response);
    }

    /**
     * 경기 좌석 조회 - 블록별
     */
    @GetMapping("/{gameId}/seats-block")
    public CommonResponse<SeatResponse> getSeatsByBlock(@PathVariable Long gameId,
                                                        @RequestParam String level) {

        List<GameSeatCountDto> seatCountList= seatService.getSeatBlockAndCounts(gameId, level);

        SeatResponse response = SeatResponse.from(seatCountList);

        return new CommonResponse<>(response);
    }

    /**
     * 예약 가능 좌석
     */
    @GetMapping("/{gameId}/seats-available")
    public CommonResponse<AvailableSeatResponse> getSeatsAvailable(@PathVariable Long gameId,
                                                                   @RequestParam String level,
                                                                   @RequestParam String block) {
        List<SeatDto> availableSeats = seatService.getAvailavleSeat(gameId, level, block);

        AvailableSeatResponse response = AvailableSeatResponse.from(availableSeats);

        return new CommonResponse<>(response);
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






    /**
     * 경기장 좌석 세팅
     */
    @GetMapping("/save")
    public void saveSeats() {
        Arrays.stream(StadiumInfo.values())
                .map(stadiumInfo -> {
                    Stadium stadium = new Stadium();
                    stadium.setId(stadiumInfo.code);
                    return SeatArrangement.generateSeats(stadium);
                })
                .forEach(seatService::saveSeats);

    }
}
