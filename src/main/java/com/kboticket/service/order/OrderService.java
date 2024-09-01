package com.kboticket.service.order;

import com.kboticket.controller.order.dto.OrderDetailResponse;
import com.kboticket.controller.order.dto.OrderListResponse;
import com.kboticket.domain.*;
import com.kboticket.dto.order.OrderDto;
import com.kboticket.dto.order.OrderSearchDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // 주문 완료
    public void completeOrder(Order order) {
        String title = order.getGame().getHomeTeam().getName() +
                " VS " +  order.getGame().getAwayTeam().getName();
        order.setStatus(OrderStatus.COMPLETE);
        order.setOrderDate(LocalDateTime.now());
        order.setName(title);
        orderRepository.save(order);
    }

    // 주문 목록
    public OrderListResponse getOrderList(OrderSearchDto orderSearchDto, String cursor, int limit) {
        List<OrderDto> orders = orderRepository.getByCursor(orderSearchDto, cursor, limit);

        return OrderListResponse.builder()
                .orders(orders)
                .build();
    }

    // 주문 상세
    public OrderDetailResponse getOrderDetails(String orderId) {
        OrderDto orderDto = orderRepository.findOrderDetailById(orderId)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_ORDER));

        return OrderDetailResponse.from(orderDto);
    }
}


