package com.kboticket.service;

import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;


    @BeforeEach
    void setUp() {
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
    }

    @Test
    void shouldReserveSeatsSuccessfully() throws InterruptedException {
        Set<Long> seatIds = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        Long gameId = 1L;
        String email = "test@example.com";

        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        reservationService.reserve(seatIds, gameId, email);

        verify(rLock, times(seatIds.size())).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        verify(rLock, times(0)).unlock();
    }

    @Test
    void shouldThrowExceptionWhenSeatIsAlreadyHeld() throws Exception {
        Set<Long> seatIds = new HashSet<>(Arrays.asList(1L, 2L));
        Long gameId = 1L;
        String email = "test@example.com";

        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        KboTicketException exception = assertThrows(KboTicketException.class, () -> {
            reservationService.reserve(seatIds, gameId, email);
        });

        assertEquals("테스트가 실패하면 메시지 표", ErrorCode.FAILED_TRY_ROCK.message, exception.getMessage());
        verify(rLock, times(1)).unlock();
    }

    @Test
    void shouldReleaseLocksOnException() throws InterruptedException {
        Set<Long> seatIds = new HashSet<>(Arrays.asList(1L, 2L));
        Long gameId = 1L;
        String email = "test@example.com";

        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true)
                .thenThrow(new KboTicketException(ErrorCode.FAILED_TRY_ROCK));

        try {
            reservationService.reserve(seatIds, gameId, email);
        } catch (KboTicketException e) {
            // Exception is expected
        }

        verify(rLock, times(1)).unlock();
    }

    @Test
    void shouldHandleInterruptedException() throws InterruptedException {
        Set<Long> seatIds = new HashSet<>(Arrays.asList(1L, 2L));
        Long gameId = 1L;
        String email = "test@example.com";

        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenThrow(new InterruptedException());

        assertDoesNotThrow(() -> reservationService.reserve(seatIds, gameId, email));

        verify(rLock, times(1)).unlock();
    }
}