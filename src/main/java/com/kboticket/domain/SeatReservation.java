package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reservation")
@Getter @Setter
public class SeatReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(name = "game_id")
    private Long gameId;


    @OneToOne(fetch = FetchType.LAZY)
    @Column(name = "stadium_id")
    private String stadiumId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

}
