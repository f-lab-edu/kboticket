package com.kboticket.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.common.constants.Constant;
import com.kboticket.common.constants.KboConstant;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.enums.TokenType;
import com.kboticket.service.login.dto.TokenDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.module.Configuration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtTokenRenewalFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String ACCESS_LOCK = KboConstant.ACCESS_LOCK;
    private final String REFRESH_LOCK = KboConstant.REFRESH_LOCK;

    public JwtTokenRenewalFilter(JwtTokenProvider jwtTokenProvider,
        RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws IOException, ServletException {
        String accessToken = request.getHeader(Constant.HEADER_AUTHORIZATION).replace(Constant.TOKEN_PREFIX, "");

        if (accessToken == null || accessToken.isEmpty()) {
            logger.info("accessToken is null");
            filterChain.doFilter(request, response);
            return;
        }

        // access token 유효한 경우 -> refresh token 재발급
        if (jwtTokenProvider.validToken(accessToken)) {
            String email = jwtTokenProvider.getEmailFromToken(accessToken);
            String newRefreshToken = jwtTokenProvider.createJwtToken(email, TokenType.REFRESH);

            log.info("new refresh token : {}", newRefreshToken);

            invalidateAndSave(newRefreshToken, email);

            filterChain.doFilter(request, response);

        } else {
            // access token 유효하지 않은 경우 -> access/refresh token 재발급
            String refreshToken = request.getHeader(Constant.X_REFRESH_TOKEN);
            if (refreshToken != null && jwtTokenProvider.validToken(refreshToken)) {
                String email = jwtTokenProvider.getEmailFromToken(refreshToken);

                String newAccessToken = jwtTokenProvider.createJwtToken(email, TokenType.ACCESS);
                String newRefreshToken = jwtTokenProvider.createJwtToken(email, TokenType.REFRESH);

                invalidateAndSave(newRefreshToken, email);

                log.info("new access token =====> {}", newAccessToken);
                log.info("new refresh token =====> {}", newRefreshToken);

                // response setting
                response.setContentType(Constant.APPLICATION_JSON);
                response.setHeader(Constant.HEADER_AUTHORIZATION, Constant.TOKEN_PREFIX + newAccessToken);

                filterChain.doFilter(request, response);
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
        }
    }

    private void invalidateAndSave(String newRefreshToken, String email) {
        String refreshKey = String.format("%s:%s", REFRESH_LOCK, email);

        invalidatePrevRefreshToken(refreshKey);

        saveToken(refreshKey, newRefreshToken, TokenType.REFRESH.getExpireTime(), TimeUnit.DAYS);
    }

    private void invalidatePrevRefreshToken(String refreshKey) {
        redisTemplate.delete(refreshKey);
    }

    private void saveToken(String key, String token, long duration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, token, duration, timeUnit);
    }

    private void reissueToken(String email, TokenType type) {
        String token = jwtTokenProvider.createJwtToken(email, type);
        String refreshKey = String.format("%s:%s", REFRESH_LOCK, email);


    }
}
