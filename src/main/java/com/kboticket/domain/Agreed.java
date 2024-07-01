package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "agreed")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agreed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumns({
        @JoinColumn(name = "title", referencedColumnName = "title"),
        @JoinColumn(name = "version", referencedColumnName = "version")})
    private Terms terms;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime agreedDate;

    public Agreed(Terms terms, User user, LocalDateTime agreedDate) {
        this.terms = terms;
        this.user = user;
        this.agreedDate = agreedDate;
    }

}