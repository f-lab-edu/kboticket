package com.kboticket.service.stadium.dto;

import com.kboticket.domain.Stadium;
import lombok.Builder;

@Builder
public class StadiumDto {

    private String id;
    private String name;
    private String address;

    public static StadiumDto from (Stadium stadium) {
        return StadiumDto.builder()
                .id(stadium.getId())
                .name(stadium.getName())
                .address(stadium.getAddress())
                .build();
    }
}
