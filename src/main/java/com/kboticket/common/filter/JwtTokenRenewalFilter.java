package com.kboticket.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.common.constants.KboConstant;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.enums.TokenType;
import com.kboticket.service.login.dto.TokenDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtTokenRenewalFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String ACCESS_LOCK = KboConstant.ACCESS_LOCK;
    private final String REFRESH_LOCK = KboConstant.REFRESH_LOCK;
    private final String BASIC_DLIIMITER = KboConstant.BASIC_DLIIMITER;

    public JwtTokenRenewalFilter(JwtTokenProvider jwtTokenProvider,
        RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws IOException, ServletException {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("X-Refresh-Token");
        if (accessToken == null || accessToken.isEmpty()) {
            logger.info("accessToken is null");
            filterChain.doFilter(request, response);
            return;
        }
        if (!jwtTokenProvider.validToken(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (refreshToken != null && jwtTokenProvider.validToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            String newAccessToken = jwtTokenProvider.createJwtToken(email, TokenType.ACCESS);
            String newRefreshToken = jwtTokenProvider.createJwtToken(email, TokenType.REFRESH);

            String accessKey = ACCESS_LOCK + BASIC_DLIIMITER + email;
            String refreshKey = REFRESH_LOCK + BASIC_DLIIMITER + email;

            invalidatePreviousToken(accessKey, refreshKey);

            saveToken(accessKey, newAccessToken, TokenType.ACCESS.getExpireTime(),
                TimeUnit.MILLISECONDS);
            saveToken(refreshKey, newRefreshToken, TokenType.REFRESH.getExpireTime(),
                TimeUnit.DAYS);

            response.setContentType("application/json");
            response.setHeader("Authorization", "Bearer " + newAccessToken);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper
                .writeValueAsString(new TokenDto(newAccessToken, newRefreshToken));
            response.getWriter().write(jsonResponse);

            filterChain.doFilter(request, response);
            return;
        }
        // 두 토큰이 모두 없는 경우 또는 유효하지 않은 경우, 인증 실패 응답
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid token");

    }

    private void invalidatePreviousToken(String accessKey, String refreshKey) {
        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);
    }

    private void saveToken(String key, String token, long duration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, token, duration, timeUnit);
    }
}
