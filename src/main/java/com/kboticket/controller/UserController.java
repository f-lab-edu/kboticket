package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.common.constants.ResponseCode;
import com.kboticket.dto.TokenDto;
import com.kboticket.dto.UserSignupRequest;
import com.kboticket.dto.response.EmailResponse;
import com.kboticket.dto.response.PasswordResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.SmsSenderService;
import com.kboticket.service.TermsService;
import com.kboticket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TermsService termsService;
    private final SmsSenderService smsSenderService;

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

        return new CommonResponse(ResponseCode.SUCCESS, null, tokenDto);
    }

    /**
     * email 중복 검사
     */
    @GetMapping("/check-email")
    public CommonResponse<Void> checkDuplicateEmail(@RequestParam String email) {
        boolean isDuplicate = userService.isExistEmail(email);

        if (isDuplicate) {
            throw new KboTicketException(ErrorCode.EMAIL_DUPLICATTE);
        }

        return new CommonResponse<>(ResponseCode.SUCCESS);
    }

    /**
     * 이메일 찾기
     */
    @GetMapping("/find/email")
    public CommonResponse<EmailResponse> findEmail(@RequestParam String phone) {
        String email = userService.findbyPhone(phone);

        return new CommonResponse<>(ResponseCode.SUCCESS, null, new EmailResponse(email));
    }

    /**
     * 패스워드 찾기 (이메일로 유저 정보 있는지 확인)
     */
    @GetMapping("/find/password")
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

        return new CommonResponse<>(ResponseCode.SUCCESS, null, new PasswordResponse(tempPassword));

    }
}
