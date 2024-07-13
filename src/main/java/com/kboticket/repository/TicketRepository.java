package com.kboticket.repository;

import com.kboticket.domain.Game;
import com.kboticket.domain.Ticket;
import com.kboticket.domain.TicketStatus;
import com.kboticket.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findById(Long id);

    List<Ticket> findAll();

    List<Ticket> findByGame(Game game);

    Optional<Ticket> findByIdAndUser(Long itemId, User user);

    boolean existsBySeatIdAndGameId(Long seatId, Long gameId);
}
