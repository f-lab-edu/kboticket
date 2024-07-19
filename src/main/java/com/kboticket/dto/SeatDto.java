package com.kboticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
}
