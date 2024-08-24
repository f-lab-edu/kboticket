package com.kboticket.repository.order;

import com.kboticket.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, OrderCustomRepository {

    Optional<Order> findById(String id);

    @Query("SELECT new com.kboticket.dto.order.OrderSearchDto(o.id, o.name, o.game.gameDate, o.orderDate, o.status, COUNT(os.id) AS cnt) " +
            " FROM Order o " +
            " LEFT JOIN OrderSeat os ON o.id = os.order.id " +
            " WHERE o.id = :id " +
            " GROUP BY o.id")
    List<Order> findAllByUserId(Long id);

}
