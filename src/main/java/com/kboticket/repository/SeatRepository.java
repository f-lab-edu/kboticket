package com.kboticket.repository;

import com.kboticket.domain.Seat;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatRepository {

    private final EntityManager em;

    public Seat findOne(Long id) {
        return em.find(Seat.class, id);
    }

    public List<Seat> findAll() {
        return em.createQuery(
                "select s from Seat s", Seat.class
        ).getResultList();
    }
}
