package com.kboticket.config.kafka.Consumer;

import com.kboticket.controller.QueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private QueueService queueService;

    @KafkaListener(topics="ticketing-queue", groupId="ticketing_group")
    public void listener(String email) {
        boolean allowed = queueService.allowEntry(email);
        if (allowed) {
            log.info("User[" + email + "] is allowed to enter the ticketing page.");
        } else {
            log.info("User[" + email + "] is still in the queue.");
        }
    }
}
