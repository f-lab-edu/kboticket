package com.kboticket.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.common.constants.Constant;
import com.kboticket.common.constants.KboConstant;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.enums.TokenType;
import com.kboticket.service.login.dto.LoginDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, Object> redisTemplate;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
        AuthenticationManager authenticationManager, RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.setAuthenticationManager(authenticationManager);
        this.redisTemplate = redisTemplate;
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

        if (failed instanceof BadCredentialsException) {         // 잘못된 사용자 명 또는 비밀 번호를 입력한 경우
            errorMessage = "Invalid username or password";
        } else if (failed instanceof LockedException) {          // 계정이 잠긴 경우
            errorMessage = "Account is locked";
        } else if (failed instanceof DisabledException) {        // 계정 비 활성화 된 경우
            errorMessage = "Account is disabled";
        } else if (failed instanceof AccountExpiredException) {  // 계정이 만료된 경우
            errorMessage = "Account has expired";
        } else {                                                 // 기타 인증 실패
            errorMessage = "Authentication failed";
        }
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        String email = authResult.getName();
        String accessToken = jwtTokenProvider.createJwtToken(email, TokenType.ACCESS);
        String refreshToken = jwtTokenProvider.createJwtToken(email, TokenType.REFRESH);

        response.setHeader(Constant.HEADER_AUTHORIZATION, Constant.TOKEN_PREFIX + accessToken);
        response.setHeader(Constant.X_REFRESH_TOKEN, refreshToken);
        response.getWriter().write(
            "{\"accessToken\":\"" + accessToken + "\", \"refreshToken\":\"" + refreshToken + "\"}");

        String refreshKey = String
            .format("%s%s%s", KboConstant.REFRESH_LOCK, KboConstant.BASIC_DLIIMITER,
                authResult.getName());

        redisTemplate.opsForValue()
            .set(refreshKey, refreshToken, TokenType.REFRESH.getExpireTime(), TimeUnit.DAYS);
    }
}
