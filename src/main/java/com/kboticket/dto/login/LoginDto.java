package com.kboticket.dto.login;


import com.kboticket.controller.login.dto.LoginRequest;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

//    public void passwordEncryption(BCryptPasswordEncoder bCryptPasswordEncoder){
//        this.password = bCryptPasswordEncoder.encode(password);
//    }

}
