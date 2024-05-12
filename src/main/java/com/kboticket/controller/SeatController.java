package com.kboticket.controller;

import com.kboticket.domain.Seat;
import com.kboticket.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/items")
    public String list(Model model) {
        List<Seat> Seats = seatService.findSeats();
        model.addAttribute("items", Seats);

        return "items/itemList";
    }
}
