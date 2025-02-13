package com.kboticket.service.reserve;

import com.kboticket.common.constants.KboConstant;
import com.kboticket.dto.ReservedSeatInfo;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    @Autowired
    private final RedissonClient redissonClient;

    @Autowired
    private final ReserveInternalService internalService;

    public void selectSeat(List<Long> seatIds, Long gameId, String email) throws InterruptedException {
        checkIfUserHasSelectedSeats(gameId, email);

        isValidateSeatsCount(seatIds);

        Collections.sort(seatIds);

        for (Long seatId : seatIds) {
            String seatKey = String.format("TICKET_%s_%s",gameId, seatId);
            internalService.lockSeat(seatKey, email, seatId);
        }
    }

    public void reserve(List<Long> seatIds, Long gameId, String email) throws Exception {
        checkIfUserHasSelectedSeats(gameId, email);

        isValidateSeatsCount(seatIds);

        List<RLock> locks = new ArrayList<>();
        try {
            for (Long seatId : seatIds) {
                String seatKey = String.format("TICKET_%s_%s",gameId, seatId);
                RLock rLock = redissonClient.getLock(seatKey);

                isSeatHold(rLock);
                boolean acquired = rLock.tryLock(KboConstant.WAIT_TIME, KboConstant.LEASE_TIME, TimeUnit.SECONDS);
                if (!acquired) {
                    throw new KboTicketException(ErrorCode.FAILED_TRY_LOCK);
                }
                locks.add(rLock);
                holdSeat(seatKey, gameId, seatId, email);
            }
        } catch (Exception e) {
            releaseLocks(locks);
            log.info("좌석 선점에 실패하였습니다.");
            throw new KboTicketException(ErrorCode.FAILED_TRY_LOCK, null, log::error);
        }

    }

    private void checkIfUserHasSelectedSeats(Long gameId, String email) {
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

    public void holdSeat(String seatKey, Long gameId, Long seatId, String email) {
        RMap<String, String> lockMap = redissonClient.getMap(seatKey);
        lockMap.put("email", email);
    }

    public String getSeat(String seatKey) {
        RMap<String, String> lockMap = redissonClient.getMap(seatKey);
        return lockMap.get(seatKey);
    }

    // 좌석 수 valid (0 < cnt <= 4)
    private void isValidateSeatsCount(List<Long> seatIds) {
        int seatCnt = seatIds.size();
        if (seatCnt == 0) {
            throw new KboTicketException(ErrorCode.EMPTY_SEATS_EXCEPTION);

        } else if (seatCnt > 4) {
            throw new KboTicketException(ErrorCode.EXCEED_SEATS_LIMIT, Map.of("seatsCount", seatIds.size()), log::info);
        }
    }

    private void releaseLocks(List<RLock> locks) {
        if (locks == null || locks.isEmpty()) return ;

        List<RLock> failedLocks = new ArrayList<>();

        for (RLock rLock : locks) {
            try {
                if (rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                }
            } catch (Exception e) {
                log.error("Failed to release lock for seat: " + rLock.getName(), e);
                failedLocks.add(rLock);
            }
        }

        if (!failedLocks.isEmpty()) {
            throw new KboTicketException(ErrorCode.FAILED_LEASE_LOCK,
                Map.of("failedLocks", failedLocks), log::error);
        }
    }



}
