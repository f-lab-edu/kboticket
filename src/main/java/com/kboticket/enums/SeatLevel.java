package com.kboticket.enums;

import com.kboticket.exception.KboTicketException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum SeatLevel {
    VIP("VIP", 49000, Arrays.asList("101", "102", "103", "104", "105", "106")),
    TABLE("TABLE", 39000,  Arrays.asList("201", "202", "203", "204", "205", "206")),
    EXCITING("EXCITING", 29000, Arrays.asList("301", "302", "303", "304", "305", "306")),
    IN("IN", 19000, Arrays.asList("401", "402", "403", "404", "405", "406")),
    OUT("OUT", 9000, Arrays.asList("501", "502", "503", "504", "505", "506"));

    public final String level;
    public final int price;
    public final List<String> blocks;

    SeatLevel(String level, int price, List<String> blocks) {
        this.level = level;
        this.price = price;
        this.blocks = blocks;
    }

    public static SeatLevel fromBlock(String block) {
        for (SeatLevel seatLevel : values()) {
            if (seatLevel.blocks.contains(block)) {
                return seatLevel;
            }
        }
        throw new KboTicketException(ErrorCode.NOT_FOUND_SEAT_BLOCK);
    }
}
