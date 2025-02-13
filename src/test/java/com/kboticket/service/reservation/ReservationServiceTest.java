package com.kboticket.service.reservation;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.kboticket.exception.KboTicketException;
import com.kboticket.service.reserve.ReservationService;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    private static final Long GAME_ID = 123L;
    private static final List<Long> SEATS = List.of(342L, 343L, 783L, 231L);
    private static final List<Long> SEAT = List.of(341L);

    @Test
    @DisplayName("단순 호출 테스트")
    public void testCallMethod() throws InterruptedException {
        reservationService.selectSeat(SEAT, GAME_ID, "user@example.com");
    }

    @Test
    @DisplayName("1000명의 사용자가 동일한 하나의 좌석 선택 - 동시성 테스트")
    public void testMultiReserve() throws InterruptedException {
        int threadNum = 1000;
        CountDownLatch latch = new CountDownLatch(threadNum);
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        for (int i = 0; i < threadNum; i++) {
            String email = i + "@naver.com";
            executorService.submit(() -> {
                try {
                    reservationService.selectSeat(SEAT, GAME_ID, email);
                    successCounter.incrementAndGet();
                } catch (Exception e) {


                    failureCounter.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertEquals(1, successCounter.get(), "하나의 사용자만 좌석 예약에 성공해야 합니다.");
        assertEquals(threadNum - 1, failureCounter.get(), "한 명을 제외한 사용자는 좌석 예약에 실패합니다.");
    }


    @Test
    @DisplayName("두 좌석에 대해 여러 사용자가 선점 시도 - 동시성 테스트")
    public void testMultiReserveOnTwoSeats() throws InterruptedException {
        int threadNum = 1000;
        CountDownLatch latch = new CountDownLatch(threadNum);
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        for (int i = 0; i < threadNum; i++) {
            String email =  "test"+ i + "@example.com";
            executorService.submit(() -> {
                try {
                    reservationService.reserve(SEATS, GAME_ID, email);
                    successCounter.incrementAndGet();

                } catch (KboTicketException e) {
                    failureCounter.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertEquals(1, successCounter.get(), "하나의 사용자만 좌석 예약에 성공해야 합니다.");
        assertEquals(threadNum - 1, failureCounter.get(), "다른 사용자는 예약에 실패해야 합니다.");
    }
}
