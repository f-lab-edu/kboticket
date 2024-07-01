package com.kboticket.controller;

import com.kboticket.service.GameService;
import com.kboticket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final GameService gameService;

    /**
     * 티켓예매 폼
     */
    @GetMapping("/order")
    public String orderForm(Model model) {
        return "order/orderForm";
    }

    /**
     * 티켓 예매
     */
    @PostMapping("/order")
    public String order(@RequestParam("userId") Long userId,
                        @RequestParam("gameId") Long gameId,
                        @RequestParam("seatIds") Long[] seatIds) {
        // 티켓
        orderService.order(userId, gameId, seatIds);
        return "";
    }



}
