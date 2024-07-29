package com.kboticket.repository;

import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;

import java.util.List;

public interface GameCustomRepository {

    List<GameResponse> getByCursor(GameSearchDto gameSearchDto, String cursor, int limit);
}
