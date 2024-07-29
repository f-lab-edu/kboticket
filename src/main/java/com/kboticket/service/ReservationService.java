package com.kboticket.service;

import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.ReservationStatus;
import com.kboticket.exception.KboTicketException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private static final long WAIT_TIME = 3 * 1000;         // 3 초
    private static final long  EXPIRED_TIME = 8 * 60 * 1000;        // 8
    private static final String SEAT_LOCK = "seatLock:";

    // 예매하고 락 풀기
    // 잡혀있다는 것만, 누가 잡았는지 모름
    // db에 써놓을 필요는 없음
    // 락 푸는 거 success -> 같은 락 유저, 보통의 스레드는 하나에서
    // 실제 잡고 있는 유저가 락을 풀어야 함. 구글링
    // 하나의 오더에 여러 개의 좌석, 좌석 10 -> payment 10 개 , orderid는 하나
    // 좌석 , 경기id, 예매 history 테이블,
    // 락 8 분이 아닌 결젝가 되는 순간 이라면? success 받은 후에 처리하면? 한명만 성
    public void reserve(Set<Long> seatIds, Long gameId, String email) {
        isValidateSeatsCount(seatIds);

        List<RLock> locks = new ArrayList<>();
        boolean lockAcquired = false;

        try {
            for (Long seatId : seatIds) {
                String seatKey = SEAT_LOCK + gameId + seatId;
                RLock rLock = redissonClient.getLock(seatKey);

                // 좌석이 선점되어있는지
                isSeatHold(rLock);

                if (!(rLock.tryLock(WAIT_TIME, EXPIRED_TIME, TimeUnit.MILLISECONDS))) {
                    throw new KboTicketException(ErrorCode.FAILED_TRY_ROCK);
                }
                locks.add(rLock);
                holdSeat(seatKey, email);
            }
            // 모든 락 성공시
            lockAcquired = true;

        } catch (InterruptedException | KboTicketException e) {
            e.printStackTrace();

        } finally {
            if (!lockAcquired) {
                for (RLock rLock : locks) {
                    rLock.unlock();
                }
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

    private void holdSeat(String seatKey, String email) {
        RMap<String, String> seatValue = redissonClient.getMap(seatKey);
        seatValue.put("status", ReservationStatus.HOLD.name());
        seatValue.put("userId", email);

        log.info("seatValue.get(status) ===== > " + seatValue.get("status"));
    }

    // 좌석 수 valid
    private void isValidateSeatsCount(Set<Long> seatIds) {
        int seatCnt = seatIds.size();
        if (seatCnt == 0) {
            throw new KboTicketException(ErrorCode.EMPTY_SEATS_EXCEPTION);

        } else if (seatCnt > 4) {
            throw new KboTicketException(ErrorCode.EXCEED_SEATS_LIMIT);
        }
    }

    public boolean isAvailableSeats(String seatKey) {
        RBucket<String> seatStatus = redissonClient.getBucket(seatKey);
        return !seatStatus.isExists();
    }

    private void releaseLocks(List<RLock> locks) {
        for (RLock lock : locks) {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
