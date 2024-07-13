package com.kboticket.util;

import com.kboticket.domain.Seat;
import com.kboticket.domain.Stadium;

import java.util.ArrayList;
import java.util.List;

public class SeatArrangement {
    public static List<Seat> generateSeats(Stadium stadium, int numberOfSeats, double radius) {
        List<Seat> seats = new ArrayList<>();
        double centerX = 0.0;
        double centerY = 0.0;


        for (int j=0; j<3; j++) {
            for (int i=0; i<numberOfSeats; i++) {
                double angle = 2 * Math.PI * i / numberOfSeats;
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                double z = j;


                Seat seat = new Seat();
                seat.setStadium(stadium);
                seat.setSeatX(x);
                seat.setSeatY(y);
                seat.setSeatZ(z);

                String level = null;
                int price = 0;
                if (x > 0 && y > 0) {
                    level = "VIP";
                    price = 39000;
                } else if (x<0 && y>0) {
                    level = "TABLE";
                    price = 29000;

                } else if (x>0 && y<0) {
                    level = "EXCITING";
                    price = 19000;

                } else {
                    level = "NORMAL";
                    price = 9000;
                }
                seat.setSeatLevel(level);
                seat.setPrice(price);

                System.out.printf("Seat %d: (%.2f, %.2f)%n", i + 1, x, y);

                seats.add(seat);
            }

        }
        return seats;
    }
}
