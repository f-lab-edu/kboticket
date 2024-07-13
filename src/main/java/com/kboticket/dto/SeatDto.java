package com.kboticket.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatDto {

    private Long id;
    private Double seatX;
    private Double seatY;
    private Double seatZ;
    private String seatLevel;
    private int price;
}
