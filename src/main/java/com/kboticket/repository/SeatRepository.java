package com.kboticket.repository;

import com.kboticket.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findById(Long id);

    List<Seat> findAll();

    List<Seat> findByStadiumId(String stadiumId);
}
