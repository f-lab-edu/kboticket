package com.kboticket.repository;

import com.kboticket.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findById(Long id);

    List<Seat> findAll();

    List<Seat> findByStadiumId(String stadiumId);

    @Query("SELECT s FROM Seat s WHERE s.stadium.id = :stadiumId AND s.id NOT IN (SELECT t.seat.id FROM Ticket t WHERE t.game.id = :gameId)")
    List<Seat> findAvailableSeats(@Param("stadiumId") String stadiumId, @Param("gameId") Long gameId);

}
