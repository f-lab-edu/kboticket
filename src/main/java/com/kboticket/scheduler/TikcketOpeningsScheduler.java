package com.kboticket.scheduler;

import com.kboticket.service.game.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikcketOpeningsScheduler {

    private final GameService gameService;

    @Scheduled(cron = "0 0 11 * * ?")
    public void ticketOpen() {
        gameService.openTicketing();
    }
}
