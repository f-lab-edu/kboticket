package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.common.constants.ResponseCode;
import com.kboticket.domain.Seat;
import com.kboticket.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/list")
    public CommonResponse<List<Seat>> list() {
        List<Seat> Seats = seatService.findAll();
        return new CommonResponse<>(ResponseCode.SUCCESS, null, Seats);
    }
}
