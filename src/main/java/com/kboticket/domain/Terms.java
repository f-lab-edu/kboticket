package com.kboticket.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Terms {

    @Id
    @EmbeddedId
    private TermsPk termsPk;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private boolean mandatory;

    @OneToMany(mappedBy = "terms")
    private List<Agreed> agreed;

    public Terms(String title, String version) {
        this.termsPk = new TermsPk(title, version);
    }

    @Builder
    public Terms(String title, String version, String content, boolean mandatory) {
        this.termsPk = new TermsPk(title, version);
        this.content = content;
        this.mandatory = mandatory;

    }
}