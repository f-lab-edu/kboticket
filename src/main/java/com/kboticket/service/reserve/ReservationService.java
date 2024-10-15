package com.kboticket.service.reserve;

import com.kboticket.common.constants.KboConstant;
import com.kboticket.config.redisson.DistributedLock;
import com.kboticket.dto.ReservedSeatInfo;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import java.util.ArrayList;
import java.util.LinkedList;
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

    public void selectSeat(Set<Long> seatIds, Long gameId, String email) throws InterruptedException {
        checkIfUserHasSelectedSeats(gameId, email);

        isValidateSeatsCount(seatIds);

        for (Long seatId : seatIds) {
            String seatKey = "TICKET_" + gameId + seatId;
            internalService.lockSeat(seatKey, email);
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
        log.info("holdseat");

        RMap<String, String> lockMap = redissonClient.getMap(seatKey);
        lockMap.put("email", email);
    }

    public String getSeat(String seatKey) {
        RMap<String, String> lockMap = redissonClient.getMap(seatKey);
        return lockMap.get(seatKey);
    }

    // 좌석 수 valid (0 < cnt <= 4)
    private void isValidateSeatsCount(Set<Long> seatIds) {
        int seatCnt = seatIds.size();
        if (seatCnt == 0) {
            // KboTicketException 어디에서 발생했ㅈ는디? 특정 파라미터로 발생하ㅏㄹ 수 있는 예외일때 seatid도 같이 보내줌,
            // 로그가 어느 클래스에서 발생? 지금은 다 global 에서 찍힘

            // Map.of("seatId", seatIds), log::info -> 에러 추적이 편함, 컨슈머를 이용해서 찍음 globalexceptiㅐ 수정해야함
            // log::info - 유저가 없는 경우 -> 로그인 하는 경우 에러, 로직에서 에러로 처리할지, 이 클래스에서 발생한 경우 빨리 처리해야한다. -> log::error
            // 추적이 필요하지 않으면 에러코드만 넘겨도된다.
            throw new KboTicketException(ErrorCode.EMPTY_SEATS_EXCEPTION);

        } else if (seatCnt > 4) {
            throw new KboTicketException(ErrorCode.EXCEED_SEATS_LIMIT, Map.of("seatsCount", seatIds.size()), log::info);
        }
    }

    private void releaseLocks(List<RLock> locks) {
        if (locks == null || locks.isEmpty()) return ;
        for (RLock rLock : locks) {
            try {
                if (rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                }
            } catch (KboTicketException e) {
                // 예외가 발생하면 무시하고 로그 기록
                log.error("Failed to release lock for seat: " + rLock.getName(), e);

                // throw new KboTicketException(ErrorCode.EMPTY_SEATS_EXCEPTION, Map.of("locks", rLock), log::error);
            }
        }
    }



}
