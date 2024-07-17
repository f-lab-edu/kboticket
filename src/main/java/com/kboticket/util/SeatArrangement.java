package com.kboticket.util;

import com.kboticket.domain.Seat;
import com.kboticket.domain.Stadium;
import com.kboticket.enums.SeatLevel;

import java.util.*;

public class SeatArrangement {
    public static List<Seat> generateSeats(Stadium stadium) {
        /*
            1 개의 블록 : A~E 열, 0~10까지 존재 (A01 ~ E10)
            seat_number = row[x] + 01
        */
        String[] blocks = {"101", "102", "103", "104", "105", "106",
                           "201", "202", "203", "204", "205", "206",
                           "301", "302", "303", "304", "305", "306",
                           "401", "402", "403", "404", "405", "406",
                           "501", "502", "503", "504", "505", "506"};
        String[] rows = {"A", "B", "C", "D", "E"};

        List<Seat> seatList = new ArrayList<>();
        for (String block : blocks) {
            SeatLevel level = SeatLevel.fromBlock(block);

            for (String row : rows) {
                for (int i = 1; i <= 10; i++) {

                    Seat seat = Seat.builder()
                            .stadium(stadium)
                            .level(level.level)
                            .price(level.price)
                            .block(block)
                            .number(row + String.format("%02d", i))
                            .seatZ(setSeatZ(level, row))
                            .build();

                    seatList.add(seat);
                }
            }
        }
        setSeatXY(seatList);

        return seatList;
    }

    private static int setSeatZ(SeatLevel level, String seatNumber) {
        if (level == SeatLevel.IN &&
                (seatNumber.startsWith("D") || seatNumber.startsWith("E"))) {
            return 2;
        }
        return 1;
    }

    private static void setSeatXY(List<Seat> seatList) {
        int x = 1;
        int y = 1;
        for (Seat seat : seatList) {
            seat.setSeatX(x);
            seat.setSeatY(y);
            if (y == 30) {
                y = 1;
                x++;
            } else {
                y++;
            }
        }
    }

}
