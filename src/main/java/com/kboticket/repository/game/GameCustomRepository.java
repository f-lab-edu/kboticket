package com.kboticket.repository.game;

import com.kboticket.controller.game.dto.GameSearchRequest;
import com.kboticket.controller.game.dto.GameDetailResponse;

import java.util.List;

public interface GameCustomRepository {

    List<GameDetailResponse> getByCursor(GameSearchRequest gameSearchRequest, String cursor, int limit);
}
