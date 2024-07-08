package com.kboticket.controller;

import com.kboticket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 티켓 예매
     */
    @PostMapping("/order")
    @ResponseStatus(HttpStatus.CREATED)
    public void order(@RequestParam("userId") Long userId,
                        @RequestParam("gameId") Long gameId,
                        @RequestParam("seatIds") Long[] seatIds) {
        orderService.order(userId, gameId, seatIds);
    }



}
