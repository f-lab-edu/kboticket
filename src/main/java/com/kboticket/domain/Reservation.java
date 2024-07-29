package com.kboticket.domain;

import com.kboticket.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="reservation", uniqueConstraints = {@UniqueConstraint(columnNames = {"reservation_id", "seat_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @Column(name = "reservation_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Builder
    public Reservation(String id, Game game, Seat seat, User user, ReservationStatus status) {
        this.id = id;
        this.game = game;
        this.seat = seat;
        this.user = user;
        this.status = status;
    }


}
