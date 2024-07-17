package com.kboticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatDto {

    private Long id;
    private Double seatX;
    private Double seatY;
    private Double seatZ;
    private String level;
    private Integer price;
}
