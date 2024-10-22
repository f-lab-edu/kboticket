package com.kboticket.controller.reservation;

import com.kboticket.dto.ReservationDto;
import com.kboticket.service.reserve.ReservationService;
import com.kboticket.service.seat.SeatService;
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
                             @RequestParam Long gameId) throws InterruptedException {

        String email = authentication.getName();

        Set<Long> seatIds = reservationDto.getSeatIds().stream()
            .collect(Collectors.toSet());

        reservationService.selectSeat(seatIds, gameId, email);

    }
}
