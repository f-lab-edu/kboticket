package com.kboticket.controller.login;

import com.kboticket.controller.login.dto.LoginRequest;
import com.kboticket.service.login.dto.LoginDto;
import com.kboticket.service.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author winnie
 */

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(@RequestBody LoginRequest request) {
        LoginDto loginDto = LoginDto.from(request);
        loginService.login(loginDto);
    }
}
