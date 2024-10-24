package com.kboticket.service.reserve;

import com.kboticket.config.redisson.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveInternalService {

    @Autowired
    private final RedissonClient redissonClient;

    @DistributedLock(key = "#lockName")
    public void lockSeat(String lockName, String email, Long seatId) {
        holdSeat(lockName, email, seatId);
    }

    public void holdSeat(String seatKey, String email, Long seatId) {
        RMap<String, String> lockMap = redissonClient.getMap(seatKey);
        lockMap.put("email", email);
        lockMap.put("seatId", seatId.toString());
    }

}
