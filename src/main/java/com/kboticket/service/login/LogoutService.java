package com.kboticket.service.login;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new KboTicketException(ErrorCode.INVALID_AUTHORIZATION);
        }

        String token =  jwtTokenProvider.resolveToken(request.getHeader("Authorization"));
        if (token != null && jwtTokenProvider.validToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);
            // 토큰 무효화 처리
            jwtTokenProvider.deleteStoredToken("access:" + email);
            jwtTokenProvider.deleteStoredToken("refresh:" + email);

        }
    }
}
