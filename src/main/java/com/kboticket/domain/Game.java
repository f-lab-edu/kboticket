package com.kboticket.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kboticket.enums.GameStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Table(name = "game", uniqueConstraints = {@UniqueConstraint(columnNames = {"home_team_id", "away_team_id", "gameDate"})})
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    @JsonIgnore
    @OneToMany(mappedBy = "game")
    private List<Reservation> reservations;

    @JoinColumn(name = "game_date")
    private String gameDate;

    @JoinColumn(name = "start_time")
    private String startTime;

    @Enumerated(value = EnumType.STRING)
    @JoinColumn(name = "game_status")
    private GameStatus gameStatus;

}
