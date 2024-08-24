package com.kboticket.repository;

import com.kboticket.domain.Payment;
import com.kboticket.repository.game.GameCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String>, GameCustomRepository {
    Optional<Payment> findById(Long paymentId);

    Optional<Payment> findByOrderId(String orderId);
}
