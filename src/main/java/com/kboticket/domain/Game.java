package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "game")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {

    @Id @GeneratedValue
    @Column(name = "game_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team homeTeam;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team awayTeam;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    private String gameDate;

    private String startTime;

    private String gameInfo;

    private int attendance;     // 관중수
}
