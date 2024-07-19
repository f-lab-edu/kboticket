package com.kboticket.controller;

import com.kboticket.dto.ReservationDto;
import com.kboticket.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void reservations(Authentication authentication,
                             @RequestBody ReservationDto reservationDto,
                             @RequestParam Long gameId) {

        String email = authentication.getName();

        reservationService.reserve(reservationDto.getSeatIds(), gameId, email);
    }



}
