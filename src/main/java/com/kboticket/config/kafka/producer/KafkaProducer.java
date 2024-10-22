package com.kboticket.config.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void create(Long gameId, String email) {
        kafkaTemplate.send("ticketing-queue", email, email);
        log.info("User added to queue: {}", email);
    }
}
