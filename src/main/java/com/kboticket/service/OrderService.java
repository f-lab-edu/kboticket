package com.kboticket.service;

import com.kboticket.domain.*;
import com.kboticket.dto.OrderResponse;
import com.kboticket.dto.OrdersDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("userId === > " + user.getId());
        List<Order> orders = orderRepository.findAllByUserId(user.getId());

        List<OrdersDto> ordersDtos = orders.stream()
                .map(order -> {
                    return OrdersDto.builder()
                            .orderId(order.getId())
                            .title(order.getName())
                            .gameDate(order.getGame().getGameDate())
                            .orderDate(order.getOrderDate())
                            .status(order.getStatus())
                            .build();
                })
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orders(ordersDtos)
                .build();
    }

    public void completeOrder(Order order) {
        String title = order.getGame().getHomeTeam().getName() +
                        " VS " +  order.getGame().getAwayTeam().getName();
        order.setStatus(OrderStatus.COMPLETE);
        order.setOrderDate(LocalDateTime.now());
        order.setName(title);
        orderRepository.save(order);
    }
}


