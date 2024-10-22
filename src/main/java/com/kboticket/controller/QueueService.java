package com.kboticket.controller;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Setter @Getter
@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedissonClient redissonClient = null;
    private final RedisTemplate<String, Object> redisTemplate;

    private final Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    private static final long FIRST_INDEX = 0;
    private static final long LAST_INDEX = -1;
    private static final long ENTRY_SIZE = 10;
    private static final String QUEUE_ID = "ticketing-queue";

    /**
     * 작업 큐에 추가
     */
    public void addToRedisQueue(String userId, Long offset) {
        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add("ticketing-queue", userId, offset);
        log.info("대기열에 추가되었습니다. {} /{}초", userId, now);
    }

    /**
     * 대기 순번 조회
     */
    public String getQueuePosition() {
        Set<Object> queue = redisTemplate.opsForZSet().range(QUEUE_ID, FIRST_INDEX, LAST_INDEX);
        for (Object user : queue) {
            if (user.toString().equals("test0")) {
                Long rank = redisTemplate.opsForZSet().rank(QUEUE_ID, user);
                log.info("'{}'님의 현재 순번은 {}번 입니다.", user, rank);
                return rank.toString();
            }
        }
        return "";
    }

   @Scheduled(fixedDelay = 1000)
   public void sendEvents(){
        enterPageFromQueue();
        getQueuePosition();
    }

    /**
     * 화면에 진입
     */
    public void enterPageFromQueue(){
        final long start = FIRST_INDEX;
        final long end = ENTRY_SIZE - 1;

        Set<Object> queue = redisTemplate.opsForZSet().range(QUEUE_ID, start, end);
        for (Object user : queue) {
            log.info(user + " 님이 곧 예매 화면으로 진입합니다.");
            redisTemplate.opsForZSet().remove(QUEUE_ID, user);
            sendEntryNotificatiton(user.toString());
        }
    }

    public SseEmitter createEmitter(String email) {
        // 연결 추가
        SseEmitter emitter = new SseEmitter(60*60*1000L);
        // Timeout이 발생하거나 완료되면 연결 목록에서 제거
        sseEmitters.put(email, emitter);
        emitter.onCompletion(() -> sseEmitters.remove(email));
        emitter.onTimeout(() -> sseEmitters.remove(email));
        return emitter;
    }

    /**
     * SSE를 통해 유저에게 알림 전송
     */
    private void sendEntryNotificatiton(String email) {
        SseEmitter emitter = sseEmitters.get(email);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event().name("yourTurn").data(true));
            emitter.complete();
            sseEmitters.remove(email);
        } catch (Exception e) {
            log.info("[SSE Exception] ====> {}" + e.getMessage());
            emitter.completeWithError(e);
        }
    }
}
