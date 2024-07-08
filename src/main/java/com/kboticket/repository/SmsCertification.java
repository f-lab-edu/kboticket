package com.kboticket.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class SmsCertification {

    private final String PREFIX = "sms:";
    private final int LIMIT_TIME =  1000 * 60 * 3;

    private final StringRedisTemplate stringRedisTemplate;

    // Redis에 저장
    public void createSmsCertification(String phone, String cerNumber) {
        stringRedisTemplate.opsForValue()
                .set(PREFIX + phone, cerNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    // 전화번호에 해당하는 인증번호 불러오기
    public String getSmsCertification(String phone) {
        return stringRedisTemplate.opsForValue()
                .get(PREFIX  + phone);
    }

    // 인증 완료 시, redis 에서 인증번호 삭제
    public boolean deleteSmsCertification(String phone) {
        return stringRedisTemplate.delete(PREFIX + phone);
    }

    // redis에 해당 휴대폰번호로 저장된 인증번호가 존재하는지 확인
    public boolean hasKey(String phone) {
        return stringRedisTemplate.hasKey(PREFIX + phone);
    }
}
