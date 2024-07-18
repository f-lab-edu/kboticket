package com.kboticket.repository;

import com.kboticket.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RerserveRepository extends JpaRepository<Reservation, Long> {

}
