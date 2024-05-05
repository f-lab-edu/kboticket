package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Stadium {

    @Id @GeneratedValue
    @Column(name = "stadium_id")
    private Long id;

    @OneToOne(mappedBy = "stadium", fetch = FetchType.LAZY)
    private Game game;

    private String name;

    private String address;

    @OneToMany(mappedBy = "stadium")
    private List<Seat> seats;
}
