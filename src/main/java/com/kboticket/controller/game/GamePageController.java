package com.kboticket.controller.game;

import com.kboticket.config.kafka.producer.KafkaProducer;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GamePageController {

    private final KafkaProducer producer;

    @GetMapping("/ticket-page/{gameId}")
    public Map<String, Object> enterQueuePage(@PathVariable Long gameId, String email) {
        producer.create(gameId, email);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully entered the queue");
        response.put("gameId", gameId);
        response.put("email", email);

        return response;
    }
}
