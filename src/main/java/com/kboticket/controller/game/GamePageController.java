package com.kboticket.controller.game;

import com.kboticket.config.kafka.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class GamePageController {

    private final KafkaProducer producer;

    @GetMapping("/ticket-page/{gameId}")
    public String enterQueuePage(@PathVariable Long gameId, String email) {
        producer.create(gameId, email);

        return "ticket-queue";
    }
}
