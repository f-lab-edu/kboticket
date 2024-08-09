package com.kboticket.repository;

import com.kboticket.domain.OrderSeat;
import com.kboticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findById(Long id);

    List<Ticket> findAll();

    Ticket findByOrderSeat(OrderSeat orderSeat);

}
