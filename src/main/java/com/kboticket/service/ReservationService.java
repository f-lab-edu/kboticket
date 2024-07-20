package com.kboticket.service;

import com.kboticket.domain.Game;
import com.kboticket.domain.Reservation;
import com.kboticket.domain.Seat;
import com.kboticket.domain.User;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.ReservationStatus;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.GameRepository;
import com.kboticket.repository.ReservationRepository;
import com.kboticket.repository.SeatRepository;
import com.kboticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reserveRepository;
    private final GameRepository gameRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;

    private static final long RESERVATION_TIMEOUT = 8 * 60 * 1000;  // 8

    public void reserve(List<Long> seatIds, Long gameId, String email) {
        // 좌석 validation
        isValidateSeatsCount(seatIds);

        // reserveSeatInRedis
        reserveSeatsInRedis(seatIds, gameId, email);

    }

    private void isValidateSeatsCount(List<Long> seatIds) {
        int seatCnt = seatIds.size();
        if (seatCnt == 0) {
            throw new KboTicketException(ErrorCode.EMPTY_SEATS_EXCEPTION);
        } else if (seatCnt > 4) {
            throw new KboTicketException(ErrorCode.EXCEED_SEATS_LIMIT);
        }
    }

    private void reserveSeatsInRedis(List<Long> seatIds, Long gameId, String email) {
        for (Long seatId : seatIds) {
            String key = "reserveKey:" + gameId +  + seatId;

            if (isSeatReserved(key)) {
                throw new KboTicketException(ErrorCode.SEAT_ALREADY_RESERVED);
            }

            redisTemplate.opsForValue().setIfAbsent(key, email, RESERVATION_TIMEOUT, TimeUnit.MILLISECONDS);

            try {
                createReservations(seatIds, gameId, email);
            } catch (KboTicketException e) {
                throw new KboTicketException(ErrorCode.FAILED_RESERVATION);
            }
        }
    }

    public boolean isSeatReserved(String key) {
        log.info("redisTemplate.hasKey(key)=======>" + redisTemplate.hasKey(key));
        return redisTemplate.hasKey(key);
    }

    private void createReservations(List<Long> seatIds, Long gameId, String email) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_GAME));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_USER));

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_SEAT_INFO));

            Reservation reservation = Reservation.builder()
                    .game(game)
                    .seat(seat)
                    .user(user)
                    .status(ReservationStatus.RESERVED)
                    .build();

            reserveRepository.save(reservation);
        }
    }
}
