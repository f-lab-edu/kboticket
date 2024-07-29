package com.kboticket.repository;

import com.kboticket.domain.Payment;
import com.kboticket.domain.User;
import com.kboticket.dto.payment.PaymentHistoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String>, GameCustomRepository {
    Optional<Payment> findById(Long paymentId);

    List<PaymentHistoryDto> findAllByUser(User user);

    Optional<Payment> findByOrderId(String orderId);
}
