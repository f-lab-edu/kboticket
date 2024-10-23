package com.kboticket.controller.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.kboticket.config.kafka.producer.KafkaProducer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GameKafkaControllerTest {

    @Autowired
    private GameController gameController;

    @Autowired
    private KafkaProducer producer;

    private Authentication authentication;

    private static final Long gameId = 123L;

    @Test
    @DisplayName("Kafka 대기열 테스트")
    public void testKafkaQueue() throws InterruptedException {
        int threadNum = 100;
        CountDownLatch latch = new CountDownLatch(threadNum);

        ExecutorService executerService = Executors.newFixedThreadPool(threadNum);

        for (int i = 0; i < threadNum; i++) {
            String email = "test" + i;
            executerService.submit(() -> {
                try {
                    producer.create(gameId, email);
                } catch (Exception e) {
                    log.info("[Exception] 사용자 {} ====> {}", email, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        Thread.sleep(100000);
        latch.await();
        executerService.shutdown();

        log.info("Remaining latch count: {}", latch.getCount());
        assertEquals(0, latch.getCount(), "All requests should be processed");
    }

    @Test
    @DisplayName("순번 조회 및 화면 진입 테스트")
    public void testGetQueueStatus() throws InterruptedException {
        int threadNum = 10;
        CountDownLatch latch = new CountDownLatch(threadNum);

        ExecutorService executerService = Executors.newFixedThreadPool(threadNum);

        for (int i = 0; i < threadNum; i++) {
            String email = "test" + i;
            executerService.submit(() -> {
                try {
                    gameController.getQueueStatus(gameId, authentication);
                } catch (Exception e) {
                    log.info("[Exception] ====> {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Thread.sleep(100000);
        executerService.shutdown();

        log.info("Remaining latch count: {}", latch.getCount());
        assertEquals(0, latch.getCount(), "All requests should be processed");
    }
}