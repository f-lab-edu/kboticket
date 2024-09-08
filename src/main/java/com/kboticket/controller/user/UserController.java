package com.kboticket.controller.user;

import com.kboticket.common.CommonResponse;
import com.kboticket.common.util.PasswordUtils;
import com.kboticket.controller.user.dto.ChangePasswordRequest;
import com.kboticket.controller.user.dto.UpdateUserRequest;
import com.kboticket.dto.user.UserDto;
import com.kboticket.dto.user.UserInfoDto;
import com.kboticket.dto.user.UserPasswordDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author winnie
 */

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 로그인한 유저 정보
     */
    @GetMapping("/view")
    public CommonResponse<UserDto> getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        UserDto userDto = userService.getUserDto(email);

        return new CommonResponse<>(userDto);
    }

    /**
     * password 확인
     */
    @PostMapping("/verify-password")
    @ResponseStatus(HttpStatus.OK)
    public void verifyPassword(Authentication authentication,
                               @RequestBody String password) {

        String email = authentication.getName();
        boolean isPasswordValid = userService.verifyPassword(email, password);

        if (!isPasswordValid) {
            throw new KboTicketException(ErrorCode.INCORRECT_PASSWORD);
        }
    }

    /**
     * 유저 정보 변경
     */
    @PostMapping("/update-info")
    @ResponseStatus(HttpStatus.OK)
    public void updateInfo(Authentication authentication,
                           @RequestBody UpdateUserRequest request) {
        String email = authentication.getName();
        UserInfoDto userInfoDto = UserInfoDto.from(request);

        userService.updateUserInfo(email, userInfoDto);
    }

    /**
     * 유저 password 변경
     */
    @PostMapping("/update-password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(Authentication authentication,
                               @RequestBody ChangePasswordRequest request) {
        String email = authentication.getName();
        UserPasswordDto userPasswordDto = UserPasswordDto.of(email, request);

        userService.updatePassword(userPasswordDto);
    }
}
