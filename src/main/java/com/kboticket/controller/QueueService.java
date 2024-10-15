package com.kboticket.controller;

import java.util.LinkedList;
import java.util.Queue;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Setter
@Service
public class QueueService {

    private Queue<String> userQueue = new LinkedList<>();

    public void addToQueue(String email) {
        userQueue.add(email);
        log.info("User[" + email + "] added to the queue");
    }

    public boolean allowEntry(String email) {
        String nextUser = userQueue.poll();

        if (nextUser != null && nextUser.equals(email)) {
            log.info("User[" + email + "] is allowed to enter.");
            return true;
        }

        log.info("User[" + email + "] is still waiting.");
        return false;
    }

    public int getQueuePosition(String email) {
        int position = 1;
        for (String id : userQueue) {
            if (id.equals(email)) {
                return position;
            }
            position++;
        }
        return -1;  // 대기열에 없는 경우
    }


}
