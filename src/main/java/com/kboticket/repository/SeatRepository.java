package com.kboticket.repository;

import com.kboticket.domain.Seat;
import com.kboticket.dto.SeatCountDto;
import com.kboticket.dto.SeatDto;
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

    @Query("SELECT new com.kboticket.dto.SeatDto(s.id, s.level, s.block, s.seatX, s.seatY, s.seatZ, s.number, s.price) " +
           "  FROM Game g" +
           "  LEFT JOIN Seat s " +
           "    ON g.stadium.id = s.stadium.id" +
           "  LEFT JOIN OrderSeat os " +
           "    ON s.id = os.seat.id " +
           " WHERE g.id = :gameId" +
           "   AND s.level = :level" +
           "   AND s.block = :block" +
           "   AND os.seat.id IS NULL")
    List<SeatDto> findAvailableSeats(@Param("gameId") Long gameId, @Param("level") String level, @Param("block") String block);

    @Query("SELECT new com.kboticket.dto.SeatCountDto(s.level AS level, COUNT(s.level) AS count )" +
            " FROM Game g " +
            " LEFT JOIN Seat s " +
            "   ON g.stadium.id = s.stadium.id" +
            " LEFT JOIN OrderSeat os " +
            "   ON s.id = os.seat.id " +
            "WHERE g.id = :gameId" +
            "  AND os.seat.id IS NULL " +
            "GROUP BY s.level")
    List<SeatCountDto> findSeatLevelsAndCounts(@Param("gameId") Long gameId);

    // 쿼리dsl 로 하나로 합치기, 너무 다르면 나누기
    @Query(" SELECT new com.kboticket.dto.SeatCountDto(s.block AS block, COUNT(s.level) AS count)" +
            "  FROM Game g" +
            "  LEFT JOIN Seat s" +
            "    ON g.stadium.id = s.stadium.id" +
            "  LEFT JOIN OrderSeat os" +
            "    ON s.id = os.seat.id" +
            " WHERE g.id = :gameId" +
            "  AND s.level = :level " +
            "  AND os.seat.id IS NULL " +
            "GROUP BY s.block")
    List<SeatCountDto> findSeatBlocksAndCounts(@Param("gameId") Long gameId, @Param("level") String level);
}