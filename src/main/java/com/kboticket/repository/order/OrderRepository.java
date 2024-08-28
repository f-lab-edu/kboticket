package com.kboticket.repository.order;

import com.kboticket.domain.Order;
import com.kboticket.dto.order.OrderDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, OrderCustomRepository {

    Optional<Order> findById(String id);

    @Query("SELECT new com.kboticket.dto.order.OrderDto(o.id, o.name, o.user.email, o.game.startTime, o.game.stadium.name AS stadiumName, o.game.gameDate, o.orderDate, o.status, o.cnt, " +
            " t.orderSeat.seat.level, t.orderSeat.seat.block, t.orderSeat.seat.number, t.price, t.ticketNumber, t.cancelAvailableAt, t.isCanceled," +
            " p.amount, p.approvedAt)"+
            "FROM Order o " +
            "LEFT JOIN Payment p ON o.id = p.orderId " +
            "LEFT JOIN OrderSeat os ON o.id = os.order.id " +
            "LEFT JOIN Ticket t ON os.id = t.orderSeat.id " +
           "WHERE o.id = :orderId")
    Optional<OrderDto> findOrderDetailById(@Param("orderId") String orderId);
}
