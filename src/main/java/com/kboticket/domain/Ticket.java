package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ticket")
@Getter @Setter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    // 티켓 생성
    public static Ticket createTicket(Game game, Seat seat) {
        Ticket ticket = new Ticket();
        ticket.setGame(game);
        ticket.setSeat(seat);
        // 전체 표수에서 예매한 개수 빼기

        return ticket;
    }

    // 티켓 취소
//    public void cancel() {
//        getSeat().cancelReserved();
//    }
}
