package com.kboticket.service.stadium;

import com.kboticket.domain.Stadium;
import com.kboticket.controller.stadium.dto.StadiumDetailResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.StadiumRepository;
import com.kboticket.service.stadium.dto.StadiumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    public StadiumDto view(String stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_STADIUM);
        });

        StadiumDto stadiumDto = StadiumDto.from(stadium);

        return stadiumDto;
    }

    public boolean isExistsId(String stadiumId) {
        return stadiumRepository.existsById(stadiumId);
    }
}
