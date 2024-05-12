package com.kboticket.service;

import com.kboticket.domain.Seat;
import com.kboticket.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<Seat> findSeats() {
        return seatRepository.findAll();
    }
}
