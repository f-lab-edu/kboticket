package com.kboticket.repository;

import com.kboticket.domain.Order;
import com.kboticket.service.OrderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findById(String id);

    List<Order> findAllByUserId(Long id);

}
