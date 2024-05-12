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

    private String seatLevel;

    private String seatBlock;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserved")
    private RsrvStatus reserved = RsrvStatus.VACANCY;

    private int price;

    public void isReserved() {
        this.reserved = RsrvStatus.RESERVED;
    }

    public void cancelReserved() {
        this.reserved = RsrvStatus.VACANCY;
    }
}
