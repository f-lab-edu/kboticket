package com.kboticket.service;

import com.kboticket.domain.*;
import com.kboticket.dto.OrderResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 생성
    public void createOrder(String orderId, Game game, List<Seat> seats, User user) {
        Order order = Order.builder()
                .id(orderId)
                .game(game)
                .user(user)
                .status(OrderStatus.ORDER)
                .build();

        List<OrderSeat> orderSeats = new ArrayList<>();
        for (Seat seat : seats) {
            OrderSeat orderSeat = OrderSeat.createOrderSeat(seat, order);
            orderSeats.add(orderSeat);
        }
        order.setOrderSeats(orderSeats);

        orderRepository.save(order);
    }

    // 주문 상태 update
    public void updateOrderStatus(String orderId, OrderStatus status) {
        Order order = getOrder(orderId);
        order.setStatus(status);

        orderRepository.save(order);
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_ORDER);
        });
    }

    public List<OrderSeat> getOrderSeats(String orderId) {
        Order order = getOrder(orderId);

        return order.getOrderSeats();
    }

    public OrderResponse getOrderList(User user) {
       List<Order> orders = orderRepository.findAllByUser(user);
        return OrderResponse.builder()
                .orders(orders)
                .build();
    }

    public void completeOrder(Order order) {
        order.setStatus(OrderStatus.COMPLETE);
        orderRepository.save(order);
    }
}


