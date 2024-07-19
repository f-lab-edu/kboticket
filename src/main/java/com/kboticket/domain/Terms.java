package com.kboticket.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kboticket.enums.TermsType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @JsonIgnore
    @OneToMany(mappedBy = "terms")
    private List<Agreed> agreed;

    @Enumerated(EnumType.STRING)
    @Column(name = "terms_type")
    private TermsType type;


    public Terms(String title, String version) {
        this.termsPk = new TermsPk(title, version);
    }

    public Terms(TermsPk termsPk) {
        this.termsPk = new TermsPk(termsPk.getTitle(), termsPk.getVersion());
    }

    @Builder
    public Terms(String title, String version, String content, boolean mandatory, TermsType type) {
        this.termsPk = new TermsPk(title, version);
        this.content = content;
        this.mandatory = mandatory;
        this.type = type;

    }
}