package com.kboticket.service;

import com.kboticket.common.constants.KboConstant;
import com.kboticket.dto.ReservedSeatInfo;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.ReservationStatus;
import com.kboticket.exception.KboTicketException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final RedissonClient redissonClient;

    public void reserve(Set<Long> seatIds, Long gameId, String email) {
        isValidateSeatsCount(seatIds);

        List<RLock> locks = new ArrayList<>();
        boolean lockAcquired = false;
        try {
            for (Long seatId : seatIds) {
                String seatKey = KboConstant.SEAT_LOCK + gameId + seatId;
                RLock rLock = redissonClient.getLock(seatKey);

                // 좌석이 선점되어있는지
                isSeatHold(rLock);

                if (!(rLock.tryLock(KboConstant.WAIT_TIME, KboConstant.EXPIRED_TIME, TimeUnit.MILLISECONDS))) {
                    throw new KboTicketException(ErrorCode.FAILED_TRY_ROCK);
                }
                locks.add(rLock);
                holdSeat(seatKey, gameId, seatId, email);
            }
            // 모든 락 성공시
            lockAcquired = true;

        } catch (InterruptedException | KboTicketException e) {
            e.printStackTrace();

        } finally {
            if (!lockAcquired) {
                releaseLocks(locks);
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
        RBucket<ReservedSeatInfo> seatBucket = redissonClient.getBucket(seatKey);
        ReservedSeatInfo seatBucketValue = ReservedSeatInfo.builder()
                .gameId(gameId)
                .seatId(seatId)
                .email(email)
                .status(ReservationStatus.HOLD)
                .reservedDate(LocalDateTime.now())
                .build();

        seatBucket.set(seatBucketValue);
    }

    // 좌석 수 valid (0 < cnt <= 4)
    private void isValidateSeatsCount(Set<Long> seatIds) {
        int seatCnt = seatIds.size();
        if (seatCnt == 0) {
            throw new KboTicketException(ErrorCode.EMPTY_SEATS_EXCEPTION);

        } else if (seatCnt > 4) {
            throw new KboTicketException(ErrorCode.EXCEED_SEATS_LIMIT);
        }
    }

    private void releaseLocks(List<RLock> locks) {
        for (RLock rLock : locks) {
            rLock.unlock();
        }
    }
}
