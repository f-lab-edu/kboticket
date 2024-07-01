package com.kboticket.service;

import com.kboticket.config.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String token =  jwtTokenProvider.resolveToken(request.getHeader("Authorization"));

        if (token != null && jwtTokenProvider.validToken(token)) {
            // 토큰 무효화 처리
            jwtTokenProvider.addToBlacklist(token);
        }

    }
}
