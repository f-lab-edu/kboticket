package com.kboticket.controller;

import com.kboticket.dto.LoginRequestDto;
import com.kboticket.dto.LoginResponseDto;
import com.kboticket.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {

        String token = loginService.login(loginRequestDto);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

}
