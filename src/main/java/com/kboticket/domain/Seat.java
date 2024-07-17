package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity

@Table(name = "seat", uniqueConstraints = {@UniqueConstraint(columnNames = {"seat_x", "seat_y", "seat_z", "stadium_id"})})
@Getter @Setter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    // 위치 정보
    @Column(name = "seat_x")
    private double seatX;

    @Column(name = "seat_y")
    private double seatY;

    @Column(name = "seat_z")
    private double seatZ;

    // 좌석 level (VIP, TABLE ...)
    @Column(name = "seat_level")
    private String seatLevel;

    // 좌석 블록 (A~Z)
    @Column(name = "seat_block")
    private String seatBlock;

    // 좌석 번호 (R01, R02 ...)
    @Column(name = "seat_number")
    private String seatNumber;

    private int price;

}
