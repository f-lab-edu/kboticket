package com.kboticket.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatDto {

    private Long id;
    private String seatX;
    private String seatY;
    private String seatZ;
    private String seatLevel;
    private int price;
}
