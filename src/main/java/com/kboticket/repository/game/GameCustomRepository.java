package com.kboticket.repository.game;

import com.kboticket.dto.game.GameSearchDto;
import com.kboticket.dto.response.GameResponse;

import java.util.List;

public interface GameCustomRepository {

    List<GameResponse> getByCursor(GameSearchDto gameSearchDto, String cursor, int limit);
}
