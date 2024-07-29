package com.kboticket.service;

import com.kboticket.enums.ErrorCode;
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
    private static final long WAIT_TIME = 10 * 1000;
    private static final long  EXPIRED_TIME = 8 * 60 * 1000;
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
        List<RLock> locks = new ArrayList<>();
        for (Long seatId : seatIds) {
            // 락ㄱ을 잡음 > 현재 소유자의 orderid
            // redis 에 기록 , order_id 가 있는지 확인 , orderid = (gameid + seatid)
            String seatKey = SEAT_LOCK + gameId + seatId;
            RLock rLock = redissonClient.getLock(seatKey);
            // 락 하나라도 실패하면 다 풀어줘야 함
            // 락이 하나 이상 생기면 데드락ㄱ이 생김 -> 락을 하ㅏ나만 잡아야 한다 / trylock
            // 8 분 어디서 걸지?
            try {
                // 락 획득 실패시 exception
                // try lock 3초
                // 그 안에 못 걸면 다 풀어줘야함
                // 결제 하ㅏㄴ 후에 바로 티켓 큐로
                // 큐 역할을 하는, 하나의 디비처럼 만들어 놓음 -> 실시간이 아니어도 되는 것들은 큐로 , 비동기,
                // 시간이 오래 안 걸리면 실시간으로, order에 gameid, seatid
                // order 는 주문이 완료되면
                // 배치, 주문을 한 날짜, 주문실패한 건 지워줘야 함
                if (!(rLock.tryLock(WAIT_TIME, EXPIRED_TIME, TimeUnit.MILLISECONDS))) {
                    throw new KboTicketException(ErrorCode.FAILED_TRY_ROCK);
                }

                holdSeat(seatKey);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void holdSeat(String seatKey) {
        log.info("getBucket(seatKey)=====>" + redissonClient.getBucket(seatKey));
        RMap<String, String> seatStatus = redissonClient.getMap("seatStatus");
        seatStatus.put(seatKey, "held");
    }

    // 좌석 수 valid
    private void isValidateSeatsCount(List<Long> seatIds) {
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
