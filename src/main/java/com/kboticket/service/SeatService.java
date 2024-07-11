package com.kboticket.service;

import com.kboticket.domain.Seat;
import com.kboticket.dto.SeatDto;
import com.kboticket.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

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
            SeatDto seatDto = SeatDto.builder()
                                .id(seat.getId())
                                .seatX(seat.getSeatX())
                                .seatY(seat.getSeatY())
                                .seatZ(seat.getSeatZ())
                                .seatLevel(seat.getSeatLevel())
                                .price(seat.getPrice())
                                .build();

            seatDtoList.add(seatDto);
        }
        return seatDtoList;
    }
}
