package com.kboticket.service;

import com.esotericsoftware.minlog.Log;
import com.kboticket.common.constants.KboConstant;
import com.kboticket.dto.ReservedSeatInfo;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.ReservationStatus;
import com.kboticket.exception.KboTicketException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final RedissonClient redissonClient;

    public void reserve(Set<Long> seatIds, Long gameId, String email) {
        // 유저가 해당 경기에 이미 선한 좌석이 존재하는 경우
        isExistSeatByUser(gameId, email);
        // 좌석 수 알맞은지 확인
        isValidateSeatsCount(seatIds);

        List<RLock> locks = new ArrayList<>();
        for (Long seatId : seatIds) {
            String seatKey = KboConstant.SEAT_LOCK + gameId + seatId;
            RLock rLock = redissonClient.getLock(seatKey);
            // 좌석이 선점되어있는지 확인
            isSeatHold(rLock);

            try {
                boolean acquired = rLock.tryLock(KboConstant.WAIT_TIME, KboConstant.EXPIRED_TIME, TimeUnit.SECONDS);
                if (!acquired) {
                    throw new KboTicketException(ErrorCode.FAILED_TRY_ROCK);
                }
                locks.add(rLock);
                holdSeat(seatKey, gameId, seatId, email);

            } catch (KboTicketException e) {
                releaseLocks(locks);
                throw new KboTicketException(ErrorCode.FAILED_TRY_ROCK, null, log::error);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                releaseLocks(locks);
            }
        }
    }

    private void isExistSeatByUser(Long gameId, String email) {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> keysIterable = keys.getKeysByPattern("seatLock:" + gameId + "*");

        for (String key : keysIterable) {
            RBucket<ReservedSeatInfo> seatBucket = redissonClient.getBucket(key);
            ReservedSeatInfo data = seatBucket.get();
            if (data != null && data.getEmail().equals(email)) {
                throw new KboTicketException(ErrorCode.EXIST_SELECTED_SEATS);
            }
        }
    }

    // 좌석에 락이 걸려있는 경우
    private void isSeatHold(RLock rLock) {
        boolean isLocked = rLock.isLocked();
        if (isLocked) {
            throw new KboTicketException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
    }

    private void holdSeat(String seatKey, Long gameId, Long seatId, String email) {
        // 게임아이디는 별도로 락과 섞이지 않게, 하나의 역할만
        RBucket<ReservedSeatInfo> seatBucket = redissonClient.getBucket(seatKey);
        ReservedSeatInfo seatBucketValue = ReservedSeatInfo.builder()
                .gameId(gameId)
                .seatId(seatId)
                .email(email)
                .status(ReservationStatus.HOLD)
                .reservedDate(LocalDateTime.now())
                .build();

        seatBucket.set(seatBucketValue, KboConstant.EXPIRED_TIME, TimeUnit.MILLISECONDS);

    }

    // 좌석 수 valid (0 < cnt <= 4)
    private void isValidateSeatsCount(Set<Long> seatIds) {
        int seatCnt = seatIds.size();
        if (seatCnt == 0) {
            // KboTicketException 어디에서 발생했ㅈ는디? 특정 파라미터로 발생하ㅏㄹ 수 있는 예외일때 seatid도 같이 보내줌,
            // 로그가 어느 클래스에서 발생? 지금은 다 global 에서 찍힘

            // Map.of("seatId", seatIds), log::info -> 에러 추적이 편함, 컨슈머를 이용해서 찍음 globalexceptiㅐㅜ 수정해야함
            // log::info - 유저가 없는 경우 -> 로그인 하는 경우 에러, 로직에서 에러로 처리할지, 이 클래스에서 발생한 경우 빨리 처리해야한다. -> log::error
            // 추적이 필요하지 않으면 에러코드만 넘겨도된다.
            throw new KboTicketException(ErrorCode.EMPTY_SEATS_EXCEPTION, Map.of("seatId", seatIds), log::info);

        } else if (seatCnt > 4) {
            throw new KboTicketException(ErrorCode.EXCEED_SEATS_LIMIT);
        }
    }

    private void releaseLocks(List<RLock> locks) {
        for (RLock rLock : locks) {
            // try catch 걸아야함, 에러가 나면 무시,로그찍기
            rLock.unlock();
        }
    }
}
