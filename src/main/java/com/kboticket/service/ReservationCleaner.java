package com.kboticket.service;

import com.kboticket.domain.Reservation;
import com.kboticket.enums.ReservationStatus;
import com.kboticket.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ReservationCleaner {

    private final RedisTemplate<String, String> redisTemplate;
    private final ReservationRepository reservationRepository;

    // 애플리케이션이 시작될 때 실행
    @PostConstruct
    @Scheduled(fixedRate = 60 * 1000)   // 1분마다 실행
    public void deleteExpiredReservations() {
        // DB에 현재 예약된 모든 키 가져오기
        List<Reservation> reservations = reservationRepository.findByStatus(ReservationStatus.RESERVED);

        // Redis에서 현재 예약된 모든 키 가져오기
        Set<String> redisKeys = redisTemplate.keys("reserveKey:*");
        if (redisKeys == null) {
            redisKeys = Set.of(); // Redis에 키가 없을 때 빈 Set으로 초기화
        }

        for (Reservation reservation : reservations) {
            String redisKey = "reserveKey:" + reservation.getGame().getId() + reservation.getSeat().getId();

            if (!redisKeys.contains(redisKey)) {
                reservationRepository.delete(reservation);
            } else {
                if (isExpired(redisKey)) {
                    redisTemplate.delete(redisKey);
                }
            }
        }
    }

    private boolean isExpired(String key) {
        Long expired = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        return (expired == null) || (expired <= 0);

    }
}
