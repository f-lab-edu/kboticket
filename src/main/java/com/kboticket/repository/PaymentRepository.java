package com.kboticket.repository;

import com.kboticket.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String>, GameCustomRepository {
    Optional<Payment> findById(String orderId);
}
