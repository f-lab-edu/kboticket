package com.kboticket.controller;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.dto.SmsRequestDto;
import com.kboticket.service.SmsSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class UserApiController {

    private final SmsSenderService smsSenderService;
    private final JwtTokenProvider jwtTokenProvider;

    // 인증 번호 발송
    @PostMapping("/send")
    public ResponseEntity<Void> sendSms(@RequestBody SmsRequestDto smsRequestDto) {

        smsSenderService.sendSMS(smsRequestDto.getPhone());
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // 인증 번호 확인
    @PostMapping("/verify")
    public ResponseEntity<Void> smsVerification(@RequestBody SmsRequestDto smsRequestDto) {
        boolean isValid = smsSenderService.verifySms(smsRequestDto);

        if (isValid) {
            String verification =jwtTokenProvider.generateToken(smsRequestDto.getPhone());
            log.info(verification);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
