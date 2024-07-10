package com.kboticket.repository;

import com.kboticket.domain.Game;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface GameCustomRepository {

    List<GameResponse> getByCursor(Pageable pageable, GameSearchDto gameSearchDto, Long cursor, int limit);
}
