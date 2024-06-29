package com.kboticket.dto;


import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
@Getter @Setter
@AllArgsConstructor
public class LoginRequestDto {

    private String username;
    private String password;

    public void passwordEncryption(BCryptPasswordEncoder bCryptPasswordEncoder){
        this.password = bCryptPasswordEncoder.encode(password);
    }

}
