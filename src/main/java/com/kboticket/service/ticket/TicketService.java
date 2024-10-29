package com.kboticket.service.ticket;

import com.kboticket.domain.Order;
import com.kboticket.domain.OrderSeat;
import com.kboticket.domain.OrderStatus;
import com.kboticket.domain.Payment;
import com.kboticket.domain.Ticket;
import com.kboticket.dto.TicketDto;
import com.kboticket.dto.payment.PaymentCancelRequest;
import com.kboticket.dto.payment.PaymentCancelResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TicketStatus;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.PaymentRepository;
import com.kboticket.repository.TicketRepository;
import com.kboticket.repository.order.OrderRepository;
import com.kboticket.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final PaymentService paymentService;

    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public void createTicket(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_ORDER);
        });
        List<OrderSeat> orderSeats = order.getOrderSeats();
        List<Ticket> tickets = orderSeats.stream()
            .map(orderSeat -> Ticket.builder()
                .orderSeat(orderSeat)
                .status(TicketStatus.ISSUED)
                .name(orderSeat.getSeat().getLevel() + " " + orderSeat.getSeat().getBlock() + " "
                    + orderSeat.getSeat().getNumber())
                .issuedAt(LocalDateTime.now())
                .price(orderSeat.getSeat().getPrice())
                .build())
            .collect(Collectors.toList());

        ticketRepository.saveAll(tickets);
    }

    public PaymentCancelResponse cancel(PaymentCancelRequest request) {
        String orderId = request.getOrderId();
        Long[] ticketIds = request.getTicketId();

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> { throw new KboTicketException(ErrorCode.NOT_FOUND_ORDER); });

        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> {throw new KboTicketException(ErrorCode.PAYMENT_NOT_FOUND); });

        payment.setCancelReason(request.getCancelReason());

        boolean isAllTicketCancelled = areAllTicketIdsMatching(ticketIds, order);

        return processPaymentCancellation(request, payment, order, ticketIds, isAllTicketCancelled);
    }

    private PaymentCancelResponse processPaymentCancellation(PaymentCancelRequest request,
        Payment payment, Order order, Long[] ticketIds, boolean isAllTicketCancelled) {
        try {
            PaymentCancelResponse response = null;
            if (isAllTicketCancelled) {
                order.setStatus(OrderStatus.CANCELLED_ALL);
                response = paymentService.paymentCancelAll(payment);
            } else {
                int cancelPrice = request.getCancelAmount();
                order.setStatus(OrderStatus.CANCELLED_PART);    // 부분 취소인 경우
                response = paymentService.paymentCancelPart(payment, cancelPrice);
            }

            if (response != null) {
                cancelTickets(ticketIds);
            }
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new KboTicketException(ErrorCode.PAYMENT_CANCEL_EXCEPTION, null, log::error);
        }
    }

    private boolean areAllTicketIdsMatching(Long[] ticketIds, Order order) {
        Set<Long> existingTicketIds = getTickets(order).stream()
            .map(TicketDto::getId)
            .collect(Collectors.toSet());

        return existingTicketIds.size() == ticketIds.length &&
            Arrays.stream(ticketIds).allMatch(existingTicketIds::contains);
    }

    public void cancelTickets(Long[] ticketIds) {
        // 티켓 상태 변경 (ISSUED -> CANCELLED)
        List<Ticket> ticketsToCancel = Arrays.stream(ticketIds)
            .map(id -> {
                return ticketRepository.findById(id).orElseThrow(() -> {
                    throw new KboTicketException(ErrorCode.NOT_FOUND_TICKET);
                });
            }).collect(Collectors.toList());

        for (Ticket ticket : ticketsToCancel) {
            if (ticket.getStatus() == TicketStatus.CANCELLED) {
                throw new KboTicketException(ErrorCode.TICKET_ALREADY_CANCELLED);
            }
            ticket.setStatus(TicketStatus.CANCELLED);
        }

        ticketRepository.saveAll(ticketsToCancel);
    }

    // 티켓 목록
    public List<TicketDto> getTickets(Order order) {
        List<OrderSeat> orderSeats = order.getOrderSeats();

        List<TicketDto> tickets = orderSeats.stream()
            .map(orderSeat -> {
                Ticket ticket = ticketRepository.findByOrderSeat(orderSeat);
                return TicketDto.builder()
                    .id(ticket.getId())
                    .orderSeat(ticket.getOrderSeat())
                    .ticketNumber(ticket.getTicketNumber())
                    .name(ticket.getName())
                    .status(ticket.getStatus())
                    .issuedAt(ticket.getIssuedAt())
                    .build();
            })
            .collect(Collectors.toList());

        return tickets;
    }

    // 티켓 록록
    public TicketDto getTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
            throw new KboTicketException(ErrorCode.NOT_FOUND_TICKET);
        });

        return TicketDto.from(ticket);
    }
}
