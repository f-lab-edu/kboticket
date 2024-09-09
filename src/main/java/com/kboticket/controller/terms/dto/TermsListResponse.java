package com.kboticket.controller.terms.dto;

import com.kboticket.dto.TermsDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TermsListResponse {
    private List<TermsDto>  termsList;

    public static TermsListResponse from(List<TermsDto> response) {
        return TermsListResponse.builder()
                .termsList(response)
                .build();
    }
}
