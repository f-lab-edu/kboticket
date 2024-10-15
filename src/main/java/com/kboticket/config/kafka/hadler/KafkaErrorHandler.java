package com.kboticket.config.kafka.hadler;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class KafkaErrorHandler implements CommonErrorHandler {

    public void handle(Exception thrownException, ConsumerRecord record, Consumer consumer) {
        System.err.println("Error processing message: " + record.value());
        thrownException.printStackTrace();
    }
}
