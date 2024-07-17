package com.kboticket.service;

import com.kboticket.domain.Game;
import com.kboticket.domain.Seat;
import com.kboticket.domain.Ticket;
import com.kboticket.dto.SeatDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.GameRepository;
import com.kboticket.repository.SeatRepository;
import com.kboticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final TicketService ticketService;
    private final GameRepository gameRepository;
    private final TicketRepository ticketRepository;

    private static final String PREFIX = "seat:";
    private static final long LOCK_EXPIRE_TIME = 3 * 60 * 1000; // 3분

    public List<Seat> findAll() {
        return seatRepository.findAll();
    }

    public Seat findOne(Long itemId) {
        Optional<Seat> optionalSeat = seatRepository.findById(itemId);
        Seat seat = optionalSeat.get();

        return seat;
    }

    public List<SeatDto> getSeatsByStadium(String stadiumId) {
        List<Seat> seats = seatRepository.findByStadiumId(stadiumId);

        List<SeatDto> seatDtoList = new ArrayList<>();
        for (Seat seat : seats) {
            SeatDto seatDto = convertToSeatDto(seat);
            seatDtoList.add(seatDto);
        }

        return seatDtoList;
    }

    // 선택 가능한 좌석
    public List<SeatDto> getAvailableSeats(String stadiumId, Long gameId) {
        List<Seat> seats = seatRepository.findAvailableSeats(stadiumId, gameId);

        List<SeatDto> seatDtoList = new ArrayList<>();
        for (Seat seat : seats) {
            SeatDto seatDto = convertToSeatDto(seat);

            seatDtoList.add(seatDto);
        }
        return seatDtoList;
    }

    // 좌석 선택
    public List<SeatDto> selectSeats(Long[] seatIds, Long userId, Long gameId){
        if (!isHoldSeats(seatIds, gameId)) {    // 선점된 자리인지
           throw new KboTicketException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        ticketService.createTicket(userId, seatIds, gameId);

        List<SeatDto> seatDtoList = new ArrayList<>();
        for (Long id : seatIds) {
            Optional<Seat> optionalSeat = seatRepository.findById(id);
            Seat seat = optionalSeat.get();

            SeatDto seatDto = SeatDto.builder()
                    .id(seat.getId())
                    .build();
            seatDtoList.add(seatDto);
        }

        return seatDtoList;
    }

    private boolean isHoldSeats(Long[] seatIds, Long gameId) {
        // 자리 선점이 가능한지 확인
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_GAME));
        List<Ticket> tickets = ticketRepository.findByGame(game);

        for (Long seatId : seatIds) {
            if (ticketRepository.existsBySeatIdAndGameId(seatId, gameId)) {
                throw new KboTicketException(ErrorCode.SEAT_ALREADY_RESERVED);
            }
        }
        return true;
    }

    // 좌석 생성 임시
    public void saveSeats(List<Seat> seatList) {
        seatRepository.saveAll(seatList);
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
}
