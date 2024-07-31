package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @Column(name = "order_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderSeat> orderSeats;

    /* 티켓 매수 */
    private int cnt;
    /* 주문 상태 */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    /* 주문 일자 */
    @Column(name = "order_date")
    private LocalDateTime orderDate;
}
