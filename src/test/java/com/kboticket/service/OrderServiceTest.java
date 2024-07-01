package com.kboticket.service;

import com.kboticket.domain.Order;
import com.kboticket.repository.GameRepository;
import com.kboticket.repository.OrderRepository;
import com.kboticket.repository.SeatRepository;
import com.kboticket.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 예매() throws Exception {
        // Given
        Long userId = 1L;
        Long gameId = 1L;
        Long[] seatIds = {2l}; // 예약할 좌석 ID 배열

        // when
        Long orderId = orderService.order(userId, gameId, seatIds);
        // then
        Order getOrder = orderRepository.findOne(orderId);
        // Then
        assertEquals("예매시 상태는 ORDER", "ORDER", getOrder.getStatus());
    }

    @Test
    public void 취소() {
        // Given
        Long orderId = 9L;

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);
    }



}