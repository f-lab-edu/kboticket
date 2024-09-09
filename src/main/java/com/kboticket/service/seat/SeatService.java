package com.kboticket.service.seat;

import com.kboticket.domain.Seat;
import com.kboticket.service.seat.dto.GameSeatCountDto;
import com.kboticket.service.seat.dto.SeatDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<SeatDto> getSeatsByStadium(String stadiumId) {
        List<Seat> seats = seatRepository.findByStadiumId(stadiumId);

        List<SeatDto> seatDtoList = new ArrayList<>();
        for (Seat seat : seats) {
            SeatDto seatDto = convertToSeatDto(seat);
            seatDtoList.add(seatDto);
        }

        return seatDtoList;
    }

    // 좌석 생성
    public void saveSeats(List<Seat> seatList) {
        seatRepository.saveAll(seatList);
    }

    public Seat getSeat(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_SEAT_INFO));
    }

    // 좌석 정보
    public SeatDto getSeatInfo(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_SEAT_INFO));

        return convertToSeatDto(seat);
    }

    private SeatDto convertToSeatDto(Seat seat) {
        return SeatDto.builder()
                .id(seat.getId())
                .seatX(seat.getSeatX())
                .seatY(seat.getSeatY())
                .seatZ(seat.getSeatZ())
                .level(seat.getLevel())
                .price(seat.getPrice())
                .build();
    }

    public List<SeatDto> getSeatsPriceByStadium(String stadiumId) {
        List<Seat> seats = seatRepository.findByStadiumId(stadiumId);

        return seats.stream()
                .map(seat -> SeatDto.builder()
                        .id(seat.getId())
                        .level(seat.getLevel())
                        .price(seat.getPrice()).build())
                .collect(Collectors.toList());
    }

    public List<SeatDto> getSeatsLocationByStadium(String stadiumId) {
        List<Seat> seats = seatRepository.findByStadiumId(stadiumId);

        return seats.stream()
                .map(seat -> SeatDto.builder()
                        .id(seat.getId())
                        .seatX(seat.getSeatX())
                        .seatY(seat.getSeatY())
                        .seatZ(seat.getSeatZ())
                        .level(seat.getLevel()).build())
                .collect(Collectors.toList());
    }


    public List<GameSeatCountDto> getSeatLevelAndCounts(Long gameId) {
        List<GameSeatCountDto> seatCountList = seatRepository.findSeatLevelsAndCounts(gameId);

        return seatCountList;
    }

    public List<GameSeatCountDto> getSeatBlockAndCounts(Long gameId, String level) {
        List<GameSeatCountDto> seatCountList = seatRepository.findSeatBlocksAndCounts(gameId, level);

        return seatCountList;
    }

    public List<SeatDto> getAvailavleSeat(Long gameId, String level, String block) {
        List<SeatDto> seatInfos = seatRepository.findAvailableSeats(gameId, level, block);

        return seatInfos;
    }
}
