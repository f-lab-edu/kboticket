package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seat")
@Getter @Setter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    @Column(name = "seat_x")
    private String seatX;

    @Column(name = "seat_y")
    private String seatY;

    @Column(name = "seat_z")
    private String seatZ;

    @Column(name = "seat_level")
    private String seatLevel;

    private int price;

}
