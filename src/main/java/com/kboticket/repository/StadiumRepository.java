package com.kboticket.repository;

import com.kboticket.domain.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, String> {
    Optional<Stadium> findById(String stadiumId);
}
