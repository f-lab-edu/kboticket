package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.dto.SmsRequestDto;
import com.kboticket.dto.TokenDto;
import com.kboticket.dto.UserSignupRequest;
import com.kboticket.dto.response.EmailResponse;
import com.kboticket.dto.response.PasswordResponse;
import com.kboticket.dto.response.VerificationResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TokenType;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.SmsSenderService;
import com.kboticket.service.TermsService;
import com.kboticket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final TermsService termsService;
    private final SmsSenderService smsSenderService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 인증 번호 발송
     */
    @PostMapping("/sms-send")
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

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@RequestBody UserSignupRequest request) {
        boolean isAgreeAllMandaotryTerms = termsService.checkAllMandatoryTermsAgreed(request.getTerms());

        if (!isAgreeAllMandaotryTerms) {
            throw new KboTicketException(ErrorCode.NOT_CHECKED_MANDATORY_TERMS);
        }
        userService.signup(request);
    }

    /**
     * 재발급
     */
    @PostMapping("/reissued")
    public CommonResponse<TokenDto> reissued(HttpServletRequest request) throws Exception {
        TokenDto tokenDto = userService.reissue(request);

        return new CommonResponse(tokenDto);
    }

    /**
     * email 중복 검사
     */
    @GetMapping("/check-email")
    @ResponseStatus(HttpStatus.OK)
    public void checkDuplicateEmail(@RequestParam String email) {
        boolean isDuplicate = userService.isExistEmail(email);

        if (isDuplicate) {
            throw new KboTicketException(ErrorCode.EMAIL_DUPLICATTE);
        }
    }

    /**
     * 이메일 찾기
     */
    @GetMapping("/find-email")
    public CommonResponse<EmailResponse> findEmail(@RequestParam String phone) {
        String email = userService.findbyPhone(phone);

        return new CommonResponse<>(new EmailResponse(email));
    }

    /**
     * 패스워드 찾기 (이메일로 유저 정보 있는지 확인)
     */
    @GetMapping("/find-password")
    public CommonResponse<PasswordResponse> findPassword(@RequestParam String email,
                                                         @RequestParam String phone) {
        boolean isExistEmail = userService.isExistEmail(email);
        boolean isExistPhone = userService.isExistPhone(phone);

        if (!isExistEmail) {
            throw new KboTicketException(ErrorCode.EMAIL_NOT_FOUND);
        }
        if (!isExistPhone) {
            throw new KboTicketException(ErrorCode.PHONE_NOT_FOUND);
        }
        String tempPassword = smsSenderService.sendResetPassword(phone);

        return new CommonResponse<>(new PasswordResponse(tempPassword));

    }

}
