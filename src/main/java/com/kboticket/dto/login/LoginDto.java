package com.kboticket.dto.login;

import com.kboticket.controller.login.dto.LoginRequest;
import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor
public class LoginDto {

    private String username;
    private String password;

    public static LoginDto from(LoginRequest request) {
        return LoginDto.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }
}
