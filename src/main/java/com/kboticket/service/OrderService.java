package com.kboticket.service;

import com.kboticket.controller.order.dto.OrderDetailResponse;
import com.kboticket.controller.order.dto.OrderListResponse;
import com.kboticket.domain.*;
import com.kboticket.dto.TicketDto;
import com.kboticket.dto.order.OrderDto;
import com.kboticket.dto.order.OrderSearchDto;
import com.kboticket.dto.payment.PaymentDto;
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
        /*
        티켓명
        예매자
        관람일시
        장소
        티켓수령장소
        예매일
        현재상태
        결제수단
        예매채널


        예매번호
        좌석등급
        권종
        좌석번호
        가격
        취소여부
        취소가능일


        결제정보
        티켓금액
        배송료
        예매수수료
        휴대폰수수료
        쿠폰할인
        부가상품
        총결제금액
        결제상세정 (신용카드, 간편결제, 금액)
         */

        Object[] orderDetail = orderRepository.findOrderDetailById(orderId)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_ORDER));

        OrderDto orderDto = OrderDto.from((Order) orderDetail[0]);
        PaymentDto paymentDto = PaymentDto.from((Payment) orderDetail[1]);

        List<Ticket> tickets = (List<Ticket>) orderDetail[2];
        List<TicketDto> ticketDtos = (List<TicketDto>) tickets.stream()
                .map(TicketDto::from);



        // 5. CommonResponse에 결과를 담아 반환
        return OrderDetailResponse.of(orderDto, paymentDto, ticketDtos);
    }
}


