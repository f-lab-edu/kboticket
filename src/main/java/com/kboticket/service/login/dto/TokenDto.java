package com.kboticket.service.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
