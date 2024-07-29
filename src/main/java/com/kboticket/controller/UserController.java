package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.UserDto;
import com.kboticket.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/view")
    public CommonResponse<UserDto> getUserInfo(Authentication authentication) {
        log.info("authentication.getName()=======>" + authentication.getName());

        UserDto userDto = userService.getUserDto(authentication.getName());

        return new CommonResponse<>(userDto);
    }
}
