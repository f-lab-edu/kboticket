package com.kboticket.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TermsPk implements Serializable {

    @Column(name = "title")
    private String title;

    @Column(name = "version")
    private String version;

}
