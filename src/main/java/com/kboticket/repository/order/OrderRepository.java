package com.kboticket.repository.order;

import com.kboticket.controller.order.dto.OrderDetailResponse;
import com.kboticket.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, OrderCustomRepository {

    Optional<Order> findById(String id);

    @Query("SELECT o, p, t " +
            "FROM Order o " +
            "LEFT JOIN Payment p ON o.id = p.orderId " +
            "LEFT JOIN OrderSeat os ON o.id = os.order.id " +
            "LEFT JOIN Ticket t ON os.id = t.orderSeat.id " +
           "WHERE o.id = :orderId")
    Optional<Object[]> findOrderDetailById(@Param("orderId") String orderId);
}
