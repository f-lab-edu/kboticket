package com.kboticket.controller.terms;

import com.kboticket.common.CommonResponse;
import com.kboticket.controller.terms.dto.TermsListResponse;
import com.kboticket.dto.TermsDto;
import com.kboticket.enums.TermsType;
import com.kboticket.service.terms.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/terms")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService termsService;

    /**
     * 약관 추가
     */
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewTerms(@RequestBody TermsDto termsDto) {

        termsService.createNewTerms(termsDto);
    }

    /**
     * 약관 리스트
     */
    @PostMapping("/latest")
    public CommonResponse<TermsListResponse> termsList(@RequestParam TermsType type) {
        List<TermsDto> response = termsService.findLatestTermsByTitle(type);

        TermsListResponse termsListResponse = TermsListResponse.from(response);

        return new CommonResponse<>(termsListResponse);
    }
}
