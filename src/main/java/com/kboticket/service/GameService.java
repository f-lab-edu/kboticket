package com.kboticket.service;

import com.kboticket.domain.Game;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import com.kboticket.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public List<GameResponse> getGameList(Pageable pageable, GameSearchDto gameSearchDto, Long cursor, int limit) {

        return gameRepository.getByCursor(pageable, gameSearchDto, cursor, limit);


    }
}
