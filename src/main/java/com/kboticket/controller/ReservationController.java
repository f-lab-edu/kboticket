package com.kboticket.controller;

import com.kboticket.dto.ReservationDto;
import com.kboticket.service.ReservationService;
import com.kboticket.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final SeatService seatService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void reservations(Authentication authentication,
                             @RequestBody ReservationDto reservationDto,
                             @RequestParam Long gameId) {

        String email = authentication.getName();

        Set<Long> seatIds = reservationDto.getSeatIds().stream()
                .map(seatService::getSeat)
                .map(seat -> seat.getId())
                .collect(Collectors.toSet());

        reservationService.reserve(seatIds, gameId, email);
    }
}
