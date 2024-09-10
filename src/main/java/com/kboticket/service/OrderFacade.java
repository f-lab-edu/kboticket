package com.kboticket.service;

import com.kboticket.domain.Order;
import com.kboticket.domain.Payment;
import com.kboticket.dto.TicketDto;
import com.kboticket.dto.payment.PaymentCancelInput;
import com.kboticket.dto.payment.PaymentCancelResponse;
import com.kboticket.service.order.OrderService;
import com.kboticket.service.payment.PaymentService;
import com.kboticket.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final TicketService ticketService;
    private final PaymentService paymentService;


    // 결제 취소
    // 가장 오류가 많이 나는 것을 먼저 처리하고
    public PaymentCancelResponse cancelTickets(PaymentCancelInput paymentCancelInput) {
        String orderId = paymentCancelInput.getOrderId();
        Long[] ticketIds = paymentCancelInput.getTicketId();
        int cancelAmount = paymentCancelInput.getCancelAmount();

        Order order = orderService.getOrder(orderId);

        Payment payment = paymentService.getPayment(orderId);
        boolean isAllTicketCancelled = areAllTicketIdsMatching(ticketIds, order);

        PaymentCancelResponse paymentCancelResponse = paymentService.cancel(order, payment, isAllTicketCancelled, cancelAmount);

        if (paymentCancelResponse != null) {
            // 민약 여기서 에러가 나면? 에러찍기
            ticketService.cancelTickets(order, ticketIds);
        }
        return paymentCancelResponse;
    }

    private boolean areAllTicketIdsMatching(Long[] ticketIds, Order order) {
        Set<Long> existingTicketIds = ticketService.getTickets(order).stream()
                .map(TicketDto::getId)
                .collect(Collectors.toSet());

        return Arrays.stream(ticketIds)
                .allMatch(existingTicketIds::contains);
    }

}
