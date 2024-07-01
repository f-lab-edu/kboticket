package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "terms")
@IdClass(TermsPk.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Terms {

    @Id
    @Column(name = "title")
    private String title;

    @Id
    @Column(name = "version")
    private String version;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private boolean mandatory;

    @OneToMany(mappedBy = "terms")
    private List<Agreed> agreed;

    public Terms(String title, String version) {
        this.title = title;
        this.version = version;
    }

    @Builder
    public Terms(String title, String version, String content, boolean mandatory) {
        this.title = title;
        this.version = version;
        this.content = content;
        this.mandatory = mandatory;

    }
}