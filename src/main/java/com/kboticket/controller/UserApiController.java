package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.common.constants.ResponseCode;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.dto.SmsRequestDto;
import com.kboticket.dto.response.VerificationResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TokenType;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.SmsSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class UserApiController {

    private final SmsSenderService smsSenderService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 인증 번호 발송
     */
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public void sendSms(@RequestBody SmsRequestDto smsRequestDto) {
        smsSenderService.sendVeritificationKey(smsRequestDto.getPhone());
    }


    /**
     * 인증 번호 확인 후 토큰 발급
     */
    @PostMapping("/verify")
    public CommonResponse<Void> smsVerification(@RequestBody SmsRequestDto smsRequestDto) {
        boolean isValid = smsSenderService.verifySms(smsRequestDto);

        if (!isValid) {
            throw new KboTicketException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        String verificationToken = jwtTokenProvider.generateToken(smsRequestDto.getPhone(), TokenType.ACCESS);
        log.info(verificationToken);

        return new CommonResponse(new VerificationResponse(verificationToken));
    }

}
