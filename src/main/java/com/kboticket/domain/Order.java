package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<Ticket> tickets = new ArrayList<>();

    private LocalDateTime orderDate;    // 예매시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public static Order createOrder(User user, List<Ticket> tickets) {
        Order order = new Order();
        order.setUser(user);
        for (Ticket t : tickets) {
            order.addOrderItem(t);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    private void addOrderItem(Ticket ticket) {
        tickets.add(ticket);
        ticket.setOrder(this);
    }

    public void cancel() {
        for (Ticket ticket : tickets) {
            ticket.cancel();
        }
    }
}
