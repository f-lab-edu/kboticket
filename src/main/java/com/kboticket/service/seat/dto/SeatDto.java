package com.kboticket.service.seat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kboticket.domain.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatDto {
    private Long id;
    private String level;
    private String block;
    private int seatX;
    private int seatY;
    private int seatZ;
    private String number;
    private int price;

    public SeatDto(Long id) {
        this.id = id;
    }

    public static SeatDto from(Seat seat) {
        return SeatDto.builder()
                .id(seat.getId())
                .level(seat.getLevel())
                .block(seat.getBlock())
                .seatX(seat.getSeatX())
                .seatY(seat.getSeatY())
                .seatZ(seat.getSeatZ())
                .number(seat.getNumber())
                .price(seat.getPrice())
                .build();
    }
}
