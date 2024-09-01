package com.kboticket.controller.terms;

import com.kboticket.common.CommonResponse;
import com.kboticket.domain.Terms;
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
    public CommonResponse<List<Terms>> termsList(@RequestParam TermsType type) {
        List<Terms> response = termsService.findLatestTermsByTitle(type);
        return new CommonResponse<>(response);
    }
}
