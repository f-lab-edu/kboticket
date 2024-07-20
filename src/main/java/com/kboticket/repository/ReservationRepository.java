package com.kboticket.repository;

import com.kboticket.domain.Reservation;
import com.kboticket.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

        List<Reservation> findByStatus(ReservationStatus reserved);
}
