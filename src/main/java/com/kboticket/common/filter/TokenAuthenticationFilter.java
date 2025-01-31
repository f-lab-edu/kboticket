package com.kboticket.common.filter;

import com.kboticket.common.constants.Constant;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 토큰의 유효성을 검사 및 인증
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(Constant.HEADER_AUTHORIZATION);

        String token = getAccessToken(authorizationHeader);

        if (token == null ) {
            logger.warn("No JWT token found in request headers");
            return ;
        }

        String accessTokenKey = String.format("blacklist:%s", token);
        if (redisTemplate.hasKey(accessTokenKey)) {
            logger.info("Invalid Authorization");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
            return ;
        }

        try {
            if (jwtTokenProvider.validToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(Constant.TOKEN_PREFIX)) {
            return authorizationHeader.substring(Constant.TOKEN_PREFIX.length());
        }
        return null;
    }

}
