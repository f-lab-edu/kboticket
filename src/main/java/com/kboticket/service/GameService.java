package com.kboticket.service;

import com.kboticket.domain.Game;
import com.kboticket.dto.GameSearchDto;
import com.kboticket.dto.response.GameResponse;
import com.kboticket.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Slice<GameResponse> getGameList(Pageable pageable, GameSearchDto gameSearchDto, String cursor) {

        return gameRepository.getByCursor(pageable, gameSearchDto, cursor);

    }
}
