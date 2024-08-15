package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Builder
@Table(name = "seat", uniqueConstraints = {@UniqueConstraint(columnNames = {"stadium_id", "seat_level", "seat_block", "seat_number"})})
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    /* 위치 정보 (x, y, z) */
    @Column(name = "seat_x")
    private int seatX;

    @Column(name = "seat_y")
    private int seatY;

    @Column(name = "seat_z")
    private int seatZ;

    /* 좌석 level (VIP, TABLE ...) */
    @Column(name = "seat_level")
    private String level;

    /* 좌석 블록 (A~Z) */
    @Column(name = "seat_block")
    private String block;

    /* 좌석 번호 (R01, R02 ...) */
    @Column(name = "seat_number")
    private String number;

    @ColumnDefault("0")
    private int price;

}
