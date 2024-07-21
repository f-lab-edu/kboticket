package com.kboticket.dto;

import com.kboticket.dto.response.GameResponse;
import lombok.*;

import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {

    private boolean hasNext;
    private List<GameResponse> games;

}
