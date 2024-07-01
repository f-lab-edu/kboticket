package com.kboticket.controller;

import com.kboticket.dto.UserSignupRequest;
import com.kboticket.dto.UserSignupResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.TermsCheckedException;
import com.kboticket.service.TermsService;
import com.kboticket.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final TermsService termsService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@RequestBody UserSignupRequest request) {
        // commonresponsivee
        boolean isAgreeAllMandaotryTerms = termsService.checkAllMandatoryTermsAgreed(request.getTerms());
        if (!isAgreeAllMandaotryTerms) {
            throw new TermsCheckedException(ErrorCode.NOT_CHCKED_MANDATORY_TERMS);
        }
        // 회원가입
        userService.signup(request);

        // restcontroller, response body
        //return ResponseEntity.status(HttpStatus.CREATED).body(response);
        // 응답값을 줘야할 경우에맘ㄴ
        // ㄷ단순액션요청일경우에는 필요없x
    }

    /**
     * email 중복 검사
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkDuplicateEmail(@RequestParam String email) {
        boolean isDuplicate = userService.isExistEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(isDuplicate);
    }

    /**
     * 이메일 찾기
     */
    @GetMapping("/find/email")
    @ResponseBody
    public ResponseEntity<String> findEmail(@RequestParam String phone) {
        String email = userService.findbyPhone(phone);

        return ResponseEntity.status(HttpStatus.OK).body(email);
    }

    /**
     * 패스워드 찾기 (이메일로 유저 정보 있는지 확인)
     */
    @GetMapping("/find/password")
    public ResponseEntity<String> findPassword(@RequestParam String email) {
        boolean isExistEmail = userService.isExistEmail(email);

        if (isExistEmail) {
            return ResponseEntity.ok("Email exists. Redirect to /find/password/auth.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }
    }

    @PostMapping("/find/send")
    public ResponseEntity<Void> sendEmail(@RequestParam String email) {
        userService.sendPasswordToEmail(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }













}
