package com.kboticket.config.kafka.Consumer;

import com.kboticket.controller.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final QueueService queueService;

    @KafkaListener(topics="ticketing-queue", groupId="ticketing_group")
    public void receive(ConsumerRecord<String, String> record) {
        if (record == null) {
            log.info("대기열이 비어 있습니다.");
        }

        long offset = record.offset();
        String email = record.value();

        processUserInQueue(offset, email);
    }

    private void processUserInQueue(long offset, String email) {
        queueService.addToRedisQueue(email, offset);
    }
}
