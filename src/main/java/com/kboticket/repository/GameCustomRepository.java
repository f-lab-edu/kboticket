package com.kboticket.repository;

import com.kboticket.domain.Game;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface GameCustomRepository {

    Slice<GameResponse> getByCursor(Pageable pageable, GameSearchDto gameSearchDto, String cursor);
}
