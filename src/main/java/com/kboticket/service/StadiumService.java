package com.kboticket.service;

import com.kboticket.domain.Stadium;
import com.kboticket.dto.StadiumDto;
import com.kboticket.repository.StadiumRepository;
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
        Optional<Stadium> optionalStadium = stadiumRepository.findById(stadiumId);
        Stadium stadium = optionalStadium.get();

        return StadiumDto.builder()
                .address(stadium.getAddress())
                .name(stadium.getName())
                .build();
    }

    public boolean isExistsId(String stadiumId) {
        return stadiumRepository.existsById(stadiumId);
    }
}
