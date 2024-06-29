package com.kboticket.repository;

import com.kboticket.domain.Seat;
import com.kboticket.domain.Ticket;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketRepository {

    private final EntityManager em;

    public Ticket findOne(Long id) {
        return em.find(Ticket.class, id);
    }

}
