package com.kboticket.dto;

import com.kboticket.domain.Terms;
import com.kboticket.enums.TermsType;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsDto {
    private String title;
    private String version;
    private String content;
    private boolean mandatory;
    private TermsType type;

    public static TermsDto from(Terms terms) {
        return TermsDto.builder()
                .title(terms.getTermsPk().getTitle())
                .version(terms.getTermsPk().getVersion())
                .content(terms.getContent())
                .mandatory(terms.isMandatory())
                .type(terms.getType())
                .build();
    }
}
