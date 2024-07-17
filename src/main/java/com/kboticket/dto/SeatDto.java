package com.kboticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatDto {

    private Long id;
    private int seatX;
    private int seatY;
    private int seatZ;
    private String level;
    private Integer price;
}
