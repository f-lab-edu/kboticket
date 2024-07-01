package com.kboticket.controller;

import com.kboticket.domain.Terms;
import com.kboticket.dto.TermsDto;
import com.kboticket.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/terms")
@RequiredArgsConstructor
public class TermsContoller {

    private final TermsService termsService;

    /**
     * 약관 추가
     */
    @PostMapping("/new")
    @ResponseBody
    public ResponseEntity<Terms> createNewTerms(@RequestBody TermsDto termsDto) {
        Terms response = termsService.createNewTerms(termsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 약관 리스트
     */
    @PostMapping("/latest")
    @ResponseBody
    public ResponseEntity<List<Terms>> termsList() {
        List<Terms> response = termsService.findLatestTermsByTitle();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
