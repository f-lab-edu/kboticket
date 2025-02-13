package com.kboticket.common.utils;


import java.util.concurrent.atomic.AtomicLong;

public class SnowFlakeIdGenerator {

    private final long epoch = 1640995200000L;  // 기준 시간 2025-01-01
    private final long machineId;
    private final AtomicLong sequence = new AtomicLong();


    private final long TIMESTAMP_BITS = 42L;
    private final long MACHINE_ID_BITS = 10L;   // workerId + datacenterId
    private final long SEQUENCE_BITS = 12L;

    public SnowFlakeIdGenerator(long machineId) {
        this.machineId = machineId;
    }

    public long generateId(Long currentTimestamp) {
        long timestamp = currentTimestamp - epoch;
        return getTimestamp(timestamp) | getMachineId(machineId) | getSequence();
    }

    public long getTimestamp(Long currentTimestamp) {
        return (currentTimestamp << (MACHINE_ID_BITS + SEQUENCE_BITS));
    }

    public long getMachineId(long machineId) {
        if (machineId >= (1 << MACHINE_ID_BITS)) {
            throw new IllegalArgumentException("Machine Id out of range! (limit : 10)");
        }
        return (machineId << SEQUENCE_BITS);
    }

    public long getSequence() {
        return sequence.getAndIncrement() & ((1 << SEQUENCE_BITS) -1);
    }
}
