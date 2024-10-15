package com.kboticket.config.kafka.producer;

import com.kboticket.controller.QueueService;
import java.awt.geom.QuadCurve2D;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final QueueService queueService;

    public void create(Long gameId, String email) {
        queueService.addToQueue(email);
        kafkaTemplate.send("ticketing-queue", gameId);
    }
}
