package com.kboticket.repository.game;

import com.kboticket.domain.Game;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, GameCustomRepository {

    Optional<Game> findById(Long id);

    List<Game> findAll();

    @Query("SELECT g " +
        "  FROM Game g " +
        " WHERE g.gameDate = :dateAfterSevenDays")
    List<Game> getTicketingOpeningsForToday(@Param("dateAfterSevenDays") String dateAfterSevenDays);
}
