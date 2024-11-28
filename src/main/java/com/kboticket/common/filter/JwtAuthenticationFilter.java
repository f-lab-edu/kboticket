package com.kboticket.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.enums.TokenType;
import com.kboticket.service.login.dto.LoginDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
        AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                    loginDto.getPassword());

            Authentication authentication = getAuthenticationManager()
                .authenticate(authenticationToken);
            log.info("Authentication successful for user: " + loginDto.getUsername());
            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Authentication failed", e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String errorMessage;

        if (failed instanceof BadCredentialsException) {
            errorMessage = "Invalid username or password";  // 잘못된 사용자명 또는 비밀번호
        } else if (failed instanceof LockedException) {
            errorMessage = "Account is locked";  // 계정이 잠겨 있음
        } else if (failed instanceof DisabledException) {
            errorMessage = "Account is disabled";  // 계정 비활성화됨
        } else if (failed instanceof AccountExpiredException) {
            errorMessage = "Account has expired";  // 계정이 만료됨
        } else {
            errorMessage = "Authentication failed";  // 기타 인증 실패
        }
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        String accessToken = jwtTokenProvider
            .createJwtToken(authResult.getName(), TokenType.ACCESS);
        String refreshToken = jwtTokenProvider
            .createJwtToken(authResult.getName(), TokenType.REFRESH);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("X-Refresh-Token", refreshToken);
        response.getWriter().write(
            "{\"accessToken\":\"" + accessToken + "\", \"refreshToken\":\"" + refreshToken + "\"}");
    }
}
